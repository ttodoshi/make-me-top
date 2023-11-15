package org.example.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.config.security.RoleService;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.dto.course.UpdateCourseDto;
import org.example.dto.event.CourseCreateEvent;
import org.example.dto.explorer.ExplorerWithRatingDto;
import org.example.dto.keeper.KeeperWithRatingDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Course;
import org.example.model.CourseTheme;
import org.example.repository.CourseRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.CourseValidatorService;
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
    private final StarSystemRepository starSystemRepository;

    private final CourseThemeService courseThemeService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;
    private final PersonService personService;

    private final ModelMapper mapper;

    private final KafkaTemplate<Integer, String> updateSystemKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteKeepersKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteRequestsKafkaTemplate;

    public CourseService(CourseRepository courseRepository, StarSystemRepository starSystemRepository,
                         CourseThemeService courseThemeService, ExplorerService explorerService, KeeperService keeperService,
                         CourseValidatorService courseValidatorService, RoleService roleService, PersonService personService,
                         ModelMapper mapper, @Qualifier("updateSystemKafkaTemplate") KafkaTemplate<Integer, String> updateSystemKafkaTemplate,
                         @Qualifier("deleteKeepersKafkaTemplate") KafkaTemplate<Integer, Integer> deleteKeepersKafkaTemplate,
                         @Qualifier("deleteRequestsKafkaTemplate") KafkaTemplate<Integer, Integer> deleteRequestsKafkaTemplate) {
        this.courseRepository = courseRepository;
        this.starSystemRepository = starSystemRepository;
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
    public Course findCourseByCourseId(Integer courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDetailedCourseInfo(Integer courseId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("course", findCourseByCourseId(courseId));
        Integer authenticatedPersonId = personService.getAuthenticatedPersonId();
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
                                        ).orElse(null));
                    });
        }
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<Integer, Course> findCoursesByCourseIdIn(List<Integer> courseIds) {
        return courseRepository.findCoursesByCourseIdIn(courseIds)
                .stream()
                .collect(Collectors.toMap(
                        Course::getCourseId,
                        c -> c
                ));
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systemIds = starSystemRepository.getSystemsByGalaxyId(galaxyId)
                .stream()
                .map(StarSystemDto::getSystemId)
                .collect(Collectors.toList());
        return courseRepository.findCoursesByCourseIdIn(systemIds);
    }

    @KafkaListener(topics = "createCourseTopic", containerFactory = "createCourseKafkaListenerContainerFactory")
    public void createCourse(CourseCreateEvent course) {
        courseRepository.save(mapper.map(course, Course.class));
    }

    @KafkaListener(topics = "updateCourseTopic", containerFactory = "updateCourseKafkaListenerContainerFactory")
    @Transactional
    public void updateCourseTitle(ConsumerRecord<Integer, String> record) {
        Course course = courseRepository.findById(record.key())
                .orElseThrow(() -> new CourseNotFoundException(record.key()));
        course.setTitle(record.value());
        courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Integer galaxyId, Integer courseId, UpdateCourseDto course) {
        Course updatedCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        courseValidatorService.validatePutRequest(galaxyId, courseId, updatedCourse);
        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());
        updateSystemName(courseId, course.getTitle());
        return courseRepository.save(updatedCourse);
    }

    private void updateSystemName(Integer courseId, String title) {
        updateSystemKafkaTemplate.send("updateSystemTopic", courseId, title);
    }

    @KafkaListener(topics = "deleteCourseTopic", containerFactory = "deleteCourseKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourse(Integer courseId) {
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

    private void deleteRequestsByCourseId(Integer courseId) {
        deleteRequestsKafkaTemplate.send("deleteCourseRegistrationRequestsTopic", courseId);
    }

    private void deleteKeepersByCourseId(Integer courseId) {
        deleteKeepersKafkaTemplate.send("deleteKeepersTopic", courseId);
    }
}
