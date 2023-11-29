package org.example.course.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.course.config.security.RoleService;
import org.example.course.dto.course.UpdateCourseDto;
import org.example.course.dto.event.CourseCreateEvent;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.exception.classes.course.CourseNotFoundException;
import org.example.course.model.Course;
import org.example.course.model.CourseTheme;
import org.example.course.repository.CourseRepository;
import org.example.course.service.validator.CourseValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    private final CourseThemeService courseThemeService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;
    private final PersonService personService;

    private final ModelMapper mapper;

    private final KafkaTemplate<Long, String> updateSystemKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteKeepersKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteRequestsKafkaTemplate;

    public CourseService(CourseRepository courseRepository, CourseThemeService courseThemeService, ExplorerService explorerService,
                         KeeperService keeperService, CourseValidatorService courseValidatorService, RoleService roleService,
                         PersonService personService, ModelMapper mapper,
                         @Qualifier("updateSystemKafkaTemplate") KafkaTemplate<Long, String> updateSystemKafkaTemplate,
                         @Qualifier("deleteKeepersKafkaTemplate") KafkaTemplate<Long, Long> deleteKeepersKafkaTemplate,
                         @Qualifier("deleteRequestsKafkaTemplate") KafkaTemplate<Long, Long> deleteRequestsKafkaTemplate) {
        this.courseRepository = courseRepository;
        this.courseThemeService = courseThemeService;
        this.explorerService = explorerService;
        this.keeperService = keeperService;
        this.courseValidatorService = courseValidatorService;
        this.roleService = roleService;
        this.personService = personService;
        this.mapper = mapper;
        this.updateSystemKafkaTemplate = updateSystemKafkaTemplate;
        this.deleteKeepersKafkaTemplate = deleteKeepersKafkaTemplate;
        this.deleteRequestsKafkaTemplate = deleteRequestsKafkaTemplate;
    }

    @Transactional(readOnly = true)
    public Course findCourseByCourseId(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findCourseByCourseIdDetailed(Long courseId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("course", findCourseByCourseId(courseId));
        Long authenticatedPersonId = personService.getAuthenticatedPersonId();
        List<ExplorerWithRatingDto> explorers = explorerService.getExplorersForCourse(courseId);
        List<KeeperWithRatingDto> keepers = keeperService.getKeepersForCourse(courseId);
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            explorers.stream()
                    // if person is studying on course
                    .filter(e -> e.getPersonId().equals(authenticatedPersonId))
                    .findAny()
                    .ifPresent(e -> {
                        response.put("you", e);
                        response.put("yourKeeper",
                                keeperService
                                        .getKeeperForExplorer(
                                                e.getExplorerId(),
                                                keepers
                                        ));
                    });
        }
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<Long, Course> findCoursesByCourseIdIn(List<Long> courseIds) {
        return courseRepository.findCoursesByCourseIdIn(courseIds)
                .stream()
                .collect(Collectors.toMap(
                        Course::getCourseId,
                        c -> c
                ));
    }

    @KafkaListener(topics = "createCourseTopic", containerFactory = "createCourseKafkaListenerContainerFactory")
    public void createCourse(CourseCreateEvent course) {
        courseRepository.save(mapper.map(course, Course.class));
    }

    @KafkaListener(topics = "updateCourseTopic", containerFactory = "updateCourseKafkaListenerContainerFactory")
    @Transactional
    public void updateCourseTitle(ConsumerRecord<Long, String> record) {
        Course course = courseRepository.findById(record.key())
                .orElseThrow(() -> new CourseNotFoundException(record.key()));
        course.setTitle(record.value());
        courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long galaxyId, Long courseId, UpdateCourseDto course) {
        Course updatedCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        courseValidatorService.validatePutRequest(galaxyId, courseId, updatedCourse);
        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());
        updateSystemName(courseId, course.getTitle());
        return courseRepository.save(updatedCourse);
    }

    private void updateSystemName(Long courseId, String title) {
        updateSystemKafkaTemplate.send("updateSystemTopic", courseId, title);
    }

    @KafkaListener(topics = "deleteCourseTopic", containerFactory = "deleteCourseKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourse(Long courseId) {
        courseThemeService.deleteDataRelatedToThemes(
                this.findCourseByCourseId(courseId)
                        .getCourseThemes()
                        .stream().map(CourseTheme::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        deleteRequestsByCourseId(courseId);
        deleteKeepersByCourseId(courseId);
        courseRepository.deleteById(courseId);
    }

    private void deleteRequestsByCourseId(Long courseId) {
        deleteRequestsKafkaTemplate.send("deleteCourseRegistrationRequestsTopic", courseId);
    }

    private void deleteKeepersByCourseId(Long courseId) {
        deleteKeepersKafkaTemplate.send("deleteKeepersTopic", courseId);
    }
}
