package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseWithProgress;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.dto.courseprogress.CoursesState;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.dto.starsystem.SystemDependencyModel;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.repository.ExplorerRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.custom.StarSystemRepository;
import org.example.service.validator.CourseProgressValidatorService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseProgressService {
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final StarSystemRepository starSystemRepository;

    private final CourseThemesProgressService courseThemesProgressService;
    private final CourseProgressValidatorService courseProgressValidatorService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;

    public CoursesState getCoursesProgressForCurrentUser(Integer galaxyId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgress> studiedCourses = new LinkedHashSet<>();
        Set<Integer> closedCourses = new LinkedHashSet<>();
        for (StarSystemDTO system : starSystemRepository.getSystemsByGalaxyId(galaxyId)) {
            Optional<Explorer> explorerOptional = explorerRepository
                    .findExplorerByPersonIdAndCourseId(authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorerOptional.isPresent()) {
                Explorer explorer = explorerOptional.get();
                studiedCourses.add(new CourseWithProgress(explorer.getCourseId(),
                        courseThemeCompletionRepository.getCourseProgress(
                                explorer.getExplorerId(), explorer.getCourseId())));
            } else if (hasUncompletedParents(authenticatedPerson.getPersonId(), system.getSystemId())) {
                closedCourses.add(system.getSystemId());
            } else {
                openedCourses.add(system.getSystemId());
            }
        }
        return CoursesState.builder()
                .personId(authenticatedPerson.getPersonId())
                .firstName(authenticatedPerson.getFirstName())
                .lastName(authenticatedPerson.getLastName())
                .patronymic(authenticatedPerson.getPatronymic())
                .openedCourses(openedCourses)
                .studiedCourses(studiedCourses)
                .closedCourses(closedCourses)
                .build();
    }

    public CourseWithThemesProgress getThemesProgressByCourseId(Integer courseId) {
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(getAuthenticatedPersonId(), courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        return courseThemesProgressService.getThemesProgress(explorer);
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public boolean hasUncompletedParents(Integer personId, Integer systemId) {
        StarSystemWithDependenciesGetResponse systemWithDependencies = starSystemRepository
                .getStarSystemWithDependencies(systemId);
        for (SystemDependencyModel system : getParentDependencies(systemWithDependencies)) {
            Optional<Explorer> explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, system.getSystemId());
            if (explorer.isEmpty() || courseThemeCompletionRepository.getCourseProgress(
                    explorer.get().getExplorerId(), explorer.get().getCourseId()) < 100) {
                return true;
            } else if (system.getIsAlternative())
                return false;
        }
        return false;
    }

    private List<SystemDependencyModel> getParentDependencies(StarSystemWithDependenciesGetResponse systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, String> leaveCourse(Integer courseId) {
        final Integer personId = getAuthenticatedPersonId();
        courseProgressValidatorService.validateLeaveCourseRequest(personId, courseId);
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        explorerRepository.deleteById(explorer.getExplorerId());
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findAcceptedCourseRegistrationRequestByPersonIdAndCourseId(personId, courseId);
        courseRegistrationRequestRepository.deleteById(request.getRequestId());
        sendGalaxyCacheRefreshMessage(courseId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Вы ушли с курса " + courseId);
        return response;
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }
}
