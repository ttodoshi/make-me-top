package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.starsystem.GetStarSystemWithDependencies;
import org.example.dto.starsystem.SystemDependencyModel;
import org.example.dto.systemprogress.CourseThemeProgressDTO;
import org.example.dto.systemprogress.PlanetCompletionDTO;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.markEX.ExplorerDoesNotNeedMarkException;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.progressEX.PlanetAlreadyCompletedException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.progress.CourseMark;
import org.example.model.progress.CourseThemeCompletion;
import org.example.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerRepository explorerRepository;
    private final PlanetProgressRepository planetProgressRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ModelMapper mapper;
    @Setter
    private String token;
    @Value("${app_galaxy_url}")
    private String GALAXY_APP_URL;

    public SystemWithPlanetsProgress getPlanetsProgressBySystemId(Integer personId, Integer systemId) {
        Course course = courseRepository.findById(systemId).orElseThrow(() -> new CourseNotFoundException(systemId));
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, systemId);
        if (explorerOptional.isEmpty())
            throw new ExplorerNotFoundException();
        Explorer explorer = explorerOptional.get();
        SystemWithPlanetsProgress systemWithPlanetsProgress = new SystemWithPlanetsProgress();
        systemWithPlanetsProgress.setCourseId(course.getCourseId());
        systemWithPlanetsProgress.setTitle(course.getTitle());
        List<PlanetCompletionDTO> planetCompletions = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Optional<CourseThemeCompletion> planetProgress = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId());
            if (planetProgress.isPresent())
                planetCompletions.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetProgress.get().getCompleted())
                );
            else {
                planetCompletions.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), false)
                );
            }
        }
        systemWithPlanetsProgress.setPlanetsWithProgress(planetCompletions);
        return systemWithPlanetsProgress;
    }

    @Transactional
    public Map<String, Object> setThemeMark(Integer planetId, MarkDTO mark) {
        saveProgress(planetId, mark);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Планета " + planetId +
                " завершена исследователем " + mark.getExplorerId());
        return response;
    }

    private void saveProgress(Integer themeId, MarkDTO mark) {
        Explorer explorer = explorerRepository.findById(mark.getExplorerId()).orElseThrow(ExplorerNotFoundException::new);
        if (hasUncompletedParents(explorer))
            throw new SystemParentsNotCompletedException(explorer.getCourseId());
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        Integer currentThemeId = getCurrentCourseThemeId(explorer.getPersonId(), explorer.getCourseId());
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        Optional<CourseThemeCompletion> courseThemeProgressOptional = planetProgressRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        if (courseThemeProgressOptional.isEmpty()) {
            planetProgressRepository.save(
                    mapper.map(
                            new CourseThemeProgressDTO(
                                    explorer.getExplorerId(), themeId, true, mark.getValue()),
                            CourseThemeCompletion.class));
        } else if (courseThemeProgressOptional.get().getCompleted()) {
            throw new PlanetAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        }
    }

    private Integer getCurrentCourseThemeId(Integer personId, Integer systemId) {
        List<PlanetCompletionDTO> planetsProgress = getPlanetsProgressBySystemId(personId, systemId).getPlanetsWithProgress();
        for (PlanetCompletionDTO planet : planetsProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return planetsProgress.get(planetsProgress.size() - 1).getCourseThemeId();
    }

    public boolean hasUncompletedParents(Explorer explorer) {
        boolean parentsUncompleted = false;
        GetStarSystemWithDependencies systemWithDependencies = getStarSystemWithDependencies(explorer.getCourseId());
        for (SystemDependencyModel system : getParentDependencies(systemWithDependencies)) {
            if (planetProgressRepository.getSystemProgress(
                    explorer.getExplorerId(), explorer.getCourseId()) < 100) {
                parentsUncompleted = true;
            } else if (system.getIsAlternative())
                return false;
        }
        return parentsUncompleted;
    }

    private GetStarSystemWithDependencies getStarSystemWithDependencies(Integer systemId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("/system/" + systemId + "?withDependencies=true")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new CourseNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GetStarSystemWithDependencies.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private List<SystemDependencyModel> getParentDependencies(GetStarSystemWithDependencies systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }

    public CourseMark setCourseMark(MarkDTO courseMark) {
        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        if (!explorerRepository.existsById(courseMark.getExplorerId()))
            throw new ExplorerNotFoundException();
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean explorerNeedMark = explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(
                        person.getPersonId()).stream()
                .anyMatch(e -> e.getExplorerId().equals(courseMark.getExplorerId()));
        if (explorerNeedMark)
            return courseMarkRepository.save(
                    new CourseMark(courseMark.getExplorerId(), new Date(), courseMark.getValue())
            );
        throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
    }
}
