package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.course.config.security.RoleService;
import org.example.course.dto.course.CourseDetailedDto;
import org.example.course.dto.course.CourseDto;
import org.example.course.dto.course.UpdateCourseDto;
import org.example.course.dto.event.CourseCreateEvent;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.exception.course.CourseNotFoundException;
import org.example.course.model.Course;
import org.example.course.repository.CourseRepository;
import org.example.course.service.CourseMarkService;
import org.example.course.service.CourseService;
import org.example.course.service.ExplorerService;
import org.example.course.service.KeeperService;
import org.example.course.service.validator.CourseValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    private final CourseMarkService courseMarkService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CourseDto findCourseByCourseId(Long courseId) {
        return courseRepository.findById(courseId)
                .map(c -> mapper.map(c, CourseDto.class))
                .orElseThrow(() -> {
                    log.warn("course by id {} not found", courseId);
                    return new CourseNotFoundException(courseId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailedDto findCourseByCourseIdDetailed(String authorizationHeader, Authentication authentication, Long courseId) {
        CourseDto course = findCourseByCourseId(courseId);

        Map<Long, ExplorerWithRatingDto> explorers = explorerService.getExplorersForCourse(authorizationHeader, courseId);
        Map<Long, KeeperWithRatingDto> keepers = keeperService.getKeepersForCourse(authorizationHeader, courseId);

        CourseDetailedDto courseDetailed = new CourseDetailedDto(
                course, explorers.values(), keepers.values()
        );

        if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.EXPLORER) &&
                explorers.containsKey((Long) authentication.getPrincipal())) {
            setCourseDetailedExplorerInfo(authorizationHeader, (Long) authentication.getPrincipal(), courseDetailed, explorers, keepers);
        }

        return courseDetailed;
    }

    private void setCourseDetailedExplorerInfo(String authorizationHeader, Long authenticatedPersonId, CourseDetailedDto courseDetailed, Map<Long, ExplorerWithRatingDto> explorers, Map<Long, KeeperWithRatingDto> keepers) {
        ExplorerWithRatingDto personExplorer = explorers.get(authenticatedPersonId);

        courseDetailed.setYou(personExplorer);
        courseDetailed.setYourKeeper(keepers.get(
                keeperService.getKeeperForExplorer(
                        authorizationHeader, personExplorer.getExplorerId()
                ).getPersonId())
        );
        courseMarkService
                .findById(authorizationHeader, personExplorer.getExplorerId())
                .ifPresent(m -> courseDetailed.setMark(m.getValue()));
    }

    @Override
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
                .orElseThrow(() -> {
                    log.warn("course by id {} not found", record.key());
                    return new CourseNotFoundException(record.key());
                });
        course.setTitle(record.value());
    }

    @Override
    @Transactional
    public CourseDto updateCourse(String authorizationHeader, Long galaxyId, Long courseId, UpdateCourseDto course) {
        Course updatedCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("course by id {} not found", courseId);
                    return new CourseNotFoundException(courseId);
                });

        courseValidatorService.validatePutRequest(authorizationHeader, galaxyId, courseId, updatedCourse);

        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());

        return mapper.map(
                courseRepository.save(updatedCourse), CourseDto.class
        );
    }

    @Override
    @KafkaListener(topics = "deleteCourseTopic", containerFactory = "deleteCourseKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
