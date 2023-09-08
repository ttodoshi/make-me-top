package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CourseWithProgressDto;
import org.example.dto.courseprogress.CourseWithThemesProgressDto;
import org.example.dto.courseprogress.CoursesStateDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.dto.starsystem.SystemDependencyModelDto;
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
public class CourseProgressServiceImpl implements CourseProgressService {
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final StarSystemRepository starSystemRepository;

    private final CourseThemesProgressService courseThemesProgressService;
    private final CourseProgressValidatorService courseProgressValidatorService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;

    @Override
    public CoursesStateDto getCoursesProgressForCurrentUser(Integer galaxyId) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> openedCourses = new LinkedHashSet<>();
        Set<CourseWithProgressDto> studiedCourses = new LinkedHashSet<>();
        Set<Integer> closedCourses = new LinkedHashSet<>();
        for (StarSystemDto system : starSystemRepository.getSystemsByGalaxyId(galaxyId)) {
            Optional<Explorer> explorerOptional = explorerRepository
                    .findExplorerByPersonIdAndCourseId(authenticatedPerson.getPersonId(), system.getSystemId());
            if (explorerOptional.isPresent()) {
                Explorer explorer = explorerOptional.get();
                studiedCourses.add(new CourseWithProgressDto(system.getSystemId(),
                        courseThemeCompletionRepository.getCourseProgress(
                                explorer.getExplorerId(), system.getSystemId())));
            } else if (hasUncompletedParents(authenticatedPerson.getPersonId(), system.getSystemId())) {
                closedCourses.add(system.getSystemId());
            } else {
                openedCourses.add(system.getSystemId());
            }
        }
        return CoursesStateDto.builder()
                .personId(authenticatedPerson.getPersonId())
                .firstName(authenticatedPerson.getFirstName())
                .lastName(authenticatedPerson.getLastName())
                .patronymic(authenticatedPerson.getPatronymic())
                .openedCourses(openedCourses)
                .studiedCourses(studiedCourses)
                .closedCourses(closedCourses)
                .build();
    }

    @Override
    public CourseWithThemesProgressDto getThemesProgressByCourseId(Integer courseId) {
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(getAuthenticatedPersonId(), courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        return courseThemesProgressService.getThemesProgress(explorer);
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    @Override
    public boolean hasUncompletedParents(Integer personId, Integer systemId) {
        GetStarSystemWithDependenciesDto systemWithDependencies = starSystemRepository
                .getStarSystemWithDependencies(systemId);
        for (SystemDependencyModelDto system : getParentDependencies(systemWithDependencies)) {
            Optional<Explorer> explorer = explorerRepository.findExplorerByPersonIdAndCourseId(personId, system.getSystemId());
            if (explorer.isEmpty() || courseThemeCompletionRepository.getCourseProgress(
                    explorer.get().getExplorerId(), system.getSystemId()) < 100) {
                return true;
            } else if (system.getIsAlternative())
                return false;
        }
        return false;
    }

    private List<SystemDependencyModelDto> getParentDependencies(GetStarSystemWithDependenciesDto systemWithDependencies) {
        return systemWithDependencies.getDependencyList()
                .stream()
                .filter(s -> s.getType().equals("parent"))
                .collect(Collectors.toList());
    }

    @Override
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
