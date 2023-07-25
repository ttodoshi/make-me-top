package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.RoleService;
import org.example.dto.courseregistration.CourseRegistrationRequestDTO;
import org.example.dto.feedback.KeeperFeedbackDTO;
import org.example.dto.feedback.PersonWithRating;
import org.example.dto.homework.HomeworkRequestDTO;
import org.example.dto.keeper.KeeperDTO;
import org.example.dto.systemprogress.CurrentCourseProgressDTO;
import org.example.dto.systemprogress.PlanetCompletionDTO;
import org.example.dto.systemprogress.SystemWithPlanetsProgress;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.progress.CourseThemeCompletion;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final PlanetProgressRepository planetProgressRepository;
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;

    private final RoleService roleService;

    @Transactional
    public Map<String, Object> getInformation() {
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            return getExplorerInformation();
        } else {
            return getKeeperInformation();
        }
    }

    public Map<String, Object> getKeeperInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", getKeeperRating(authenticatedPersonId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(authenticatedPersonId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(authenticatedPersonId));
        response.put("studyingExplorers", explorerRepository.getStudyingPeopleByKeeperPersonId(authenticatedPersonId));
        response.put("studyRequests", courseRegistrationRequestRepository.getStudyRequestsByKeeperPersonId(authenticatedPersonId));
        response.put("finalAssessments", explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(authenticatedPersonId));
        response.put("reviewRequests", homeworkRequestRepository.getReviewRequestsByKeeperPersonId(authenticatedPersonId));
        return response;
    }

    private Double getKeeperRating(Integer personId) {
        return Math.ceil(explorerFeedbackRepository.getKeeperRating(personId).orElse(0.0) * 10) / 10;
    }

    public Map<String, Object> getExplorerInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", getExplorerRating(authenticatedPersonId));
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(authenticatedPersonId));
        response.put("currentSystem", getCurrentSystemProgress(authenticatedPersonId));
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(authenticatedPersonId));
        response.put("ratingTable", getRatingTable());
        return response;
    }

    private CurrentCourseProgressDTO getCurrentSystemProgress(Integer personId) {
        Integer currentSystemId = planetProgressRepository.getCurrentInvestigatedSystemId(personId);
        if (currentSystemId == null)
            return null;
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, currentSystemId);
        Explorer explorer;
        if (explorerOptional.isEmpty())
            return null;
        else
            explorer = explorerOptional.get();
        Double progress = planetProgressRepository.getSystemProgress(explorer.getExplorerId(), currentSystemId);
        Integer currentThemeId = getCurrentCourseThemeId(personId, currentSystemId);
        CourseTheme currentTheme = courseThemeRepository.findById(currentThemeId).orElseThrow(() -> new CourseThemeNotFoundException(currentThemeId));
        Course currentCourse = courseRepository.getReferenceById(currentSystemId);
        KeeperDTO keeper = keeperRepository.getKeeperForPersonOnCourse(personId, currentSystemId);
        return new CurrentCourseProgressDTO(explorer.getExplorerId(), currentTheme.getCourseThemeId(), currentTheme.getTitle(), currentCourse.getCourseId(), currentCourse.getTitle(), keeper, progress);
    }

    private Integer getCurrentCourseThemeId(Integer personId, Integer systemId) {
        List<PlanetCompletionDTO> planetsProgress = getPlanetsProgressBySystemId(personId, systemId).getPlanetsWithProgress();
        for (PlanetCompletionDTO planet : planetsProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return planetsProgress.get(planetsProgress.size() - 1).getCourseThemeId();
    }

    private SystemWithPlanetsProgress getPlanetsProgressBySystemId(Integer personId, Integer systemId) {
        Course course = courseRepository.findById(systemId).orElseThrow(() -> new CourseNotFoundException(systemId));
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, systemId);
        if (explorerOptional.isEmpty())
            throw new ExplorerNotFoundException();
        Explorer explorer = explorerOptional.get();
        SystemWithPlanetsProgress systemWithPlanetsProgress = new SystemWithPlanetsProgress();
        systemWithPlanetsProgress.setCourseId(course.getCourseId());
        systemWithPlanetsProgress.setTitle(course.getTitle());
        List<PlanetCompletionDTO> planetCompletionDTOS = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(systemId)) {
            Optional<CourseThemeCompletion> planetCompletion = planetProgressRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId());
            if (planetCompletion.isPresent())
                planetCompletionDTOS.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetCompletion.get().getCompleted())
                );
            else {
                planetCompletionDTOS.add(
                        new PlanetCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), false)
                );
            }
        }
        systemWithPlanetsProgress.setPlanetsWithProgress(planetCompletionDTOS);
        return systemWithPlanetsProgress;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private Double getExplorerRating(Integer personId) {
        return Math.ceil(keeperFeedbackRepository.getExplorerRating(personId).orElse(0.0) * 10) / 10;
    }

    private List<PersonWithRating> getRatingTable() {
        return keeperFeedbackRepository.getRatingTable();
    }

    @Transactional
    public Map<String, Object> getKeeperPublicInformation(Integer personId) {
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", getKeeperRating(personId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(personId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(personId));
        response.put("systems", courseRepository.findCoursesByKeeperPersonId(personId));
        response.put("feedback", explorerFeedbackRepository.getKeeperCommentsByPersonId(personId));
        return response;
    }

    @Transactional
    public Map<String, Object> getExplorerPublicInformation(Integer personId) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", getExplorerRating(personId));
        List<KeeperFeedbackDTO> feedback = keeperFeedbackRepository.getExplorerCommentsByPersonId(personId);
        response.put("totalFeedback", feedback.size());
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(personId));
        CurrentCourseProgressDTO currentCourse = getCurrentSystemProgress(personId);
        if (currentCourse == null) {
            Optional<CourseRegistrationRequestDTO> studyRequestOptional = courseRegistrationRequestRepository.getStudyRequestByPersonId(personId);
            if (studyRequestOptional.isPresent() && keeperRepository.getReferenceById(studyRequestOptional.get().getKeeperId()).getPersonId().equals(authenticatedPersonId)) {
                response.put("studyRequest", studyRequestOptional.get());
            }
        } else {
            if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.KEEPER)) {
                List<HomeworkRequestDTO> homeworkRequests = homeworkRequestRepository.getReviewRequestsByKeeperPersonId(authenticatedPersonId).stream().filter(h -> h.getPersonId().equals(personId)).collect(Collectors.toList());
                if (!homeworkRequests.isEmpty())
                    response.put("reviewRequests", homeworkRequests);
            }
            response.put("currentSystem", currentCourse);
        }
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(personId));
        response.put("feedback", feedback);
        return response;
    }
}
