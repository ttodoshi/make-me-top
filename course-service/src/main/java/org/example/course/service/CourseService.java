package org.example.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.course.config.security.RoleService;
import org.example.course.dto.course.CourseDetailedDto;
import org.example.course.dto.course.CourseDto;
import org.example.course.dto.course.UpdateCourseDto;
import org.example.course.dto.event.CourseCreateEvent;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.exception.classes.course.CourseNotFoundException;
import org.example.course.model.Course;
import org.example.course.repository.CourseRepository;
import org.example.course.service.validator.CourseValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;
    private final PersonService personService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public CourseDto findCourseByCourseId(Long courseId) {
        return courseRepository.findById(courseId)
                .map(c -> mapper.map(c, CourseDto.class))
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @Transactional(readOnly = true)
    public CourseDetailedDto findCourseByCourseIdDetailed(Long courseId) {
        CourseDto course = findCourseByCourseId(courseId);

        List<ExplorerWithRatingDto> explorers = explorerService.getExplorersForCourse(courseId);
        List<KeeperWithRatingDto> keepers = keeperService.getKeepersForCourse(courseId);

        CourseDetailedDto courseDetailed = new CourseDetailedDto(
                course,
                explorers,
                keepers
        );

        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            // TODO: переписать логику
            Long authenticatedPersonId = personService.getAuthenticatedPersonId();
            explorers.stream()
                    // if person is studying on course
                    .filter(e -> e.getPersonId().equals(authenticatedPersonId))
                    .findAny()
                    .ifPresent(e -> {
                        courseDetailed.setYou(e);
                        courseDetailed.setYourKeeper(keeperService
                                .getKeeperForExplorer(
                                        e.getExplorerId(),
                                        keepers
                                ));
                    });
        }

        return courseDetailed;
    }

    @Transactional(readOnly = true)
    public Map<Long, CourseDto> findCoursesByCourseIdIn(List<Long> courseIds) {
        return courseRepository.findCoursesByCourseIdIn(courseIds)
                .stream()
                .collect(Collectors.toMap(
                        Course::getCourseId,
                        c -> mapper.map(c, CourseDto.class)
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
    public CourseDto updateCourse(Long galaxyId, Long courseId, UpdateCourseDto course) {
        Course updatedCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        courseValidatorService.validatePutRequest(galaxyId, courseId, updatedCourse);

        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());

        return mapper.map(
                courseRepository.save(updatedCourse),
                CourseDto.class
        );
    }

    @KafkaListener(topics = "deleteCourseTopic", containerFactory = "deleteCourseKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
