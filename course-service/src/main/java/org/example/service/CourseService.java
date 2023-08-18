package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.event.CourseCreateEvent;
import org.example.dto.explorer.ExplorerWithRating;
import org.example.dto.keeper.KeeperCreateRequest;
import org.example.dto.keeper.KeeperWithRating;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.*;
import org.example.service.validator.CourseValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final StarSystemRepository starSystemRepository;
    private final RatingRepository ratingRepository;

    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final ModelMapper mapper;

    public Map<String, Object> getCourse(Integer courseId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("course", courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId)));
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        List<ExplorerWithRating> explorers = getExplorersForCourse(courseId);
        Collections.sort(explorers);
        List<KeeperWithRating> keepers = getKeepersForCourse(courseId);
        Collections.sort(keepers);
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            explorers.stream()
                    // if person is studying on course
                    .filter(e -> e.getPersonId().equals(authenticatedPersonId))
                    .findAny()
                    .ifPresent(e -> {
                        response.put("you", e);
                        KeeperWithRating yourKeeper = mapper.map(keeperRepository.getKeeperForPersonOnCourse(e.getPersonId(), courseId), KeeperWithRating.class);
                        yourKeeper.setRating(ratingRepository.getKeeperRating(yourKeeper.getPersonId()));
                        response.put("yourKeeper", yourKeeper);
                    });
        }
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    private List<ExplorerWithRating> getExplorersForCourse(Integer courseId) {
        List<ExplorerWithRating> explorers = new ArrayList<>();
        explorerRepository.getExplorersByCourseId(courseId).forEach(
                e -> {
                    ExplorerWithRating explorer = mapper.map(e, ExplorerWithRating.class);
                    explorer.setRating(ratingRepository.getExplorerRating(e.getPersonId()));
                    explorers.add(explorer);
                }
        );
        return explorers;
    }

    private List<KeeperWithRating> getKeepersForCourse(Integer courseId) {
        List<KeeperWithRating> keepers = new ArrayList<>();
        keeperRepository.getKeepersByCourseId(courseId).forEach(
                k -> {
                    KeeperWithRating keeper = mapper.map(k, KeeperWithRating.class);
                    keeper.setRating(ratingRepository.getKeeperRating(k.getPersonId()));
                    keepers.add(keeper);
                }
        );
        return keepers;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systems = Arrays.stream(starSystemRepository.getSystemsByGalaxyId(galaxyId))
                .mapToInt(StarSystemDTO::getSystemId)
                .boxed().collect(Collectors.toList());
        return courseRepository.findAll()
                .stream()
                .filter(c -> systems.contains(c.getCourseId()))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "courseTopic", containerFactory = "courseKafkaListenerContainerFactory")
    public void createCourse(CourseCreateEvent course) {
        courseRepository.save(mapper.map(course, Course.class));
    }

    public Course updateCourse(Integer galaxyId, Integer courseId, CourseUpdateRequest course) {
        Course updatedCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        courseValidatorService.validatePutRequest(galaxyId, courseId, updatedCourse);
        return courseRepository.save(
                updatedCourse
                        .withTitle(course.getTitle())
                        .withDescription(course.getDescription())
        );
    }

    public Keeper setKeeperToCourse(Integer courseId, KeeperCreateRequest keeperCreateRequest) {
        courseValidatorService.validateSetKeeperRequest(courseId, keeperCreateRequest);
        sendGalaxyCacheRefreshMessage(courseId);
        return keeperRepository.save(
                Keeper.builder()
                        .courseId(courseId)
                        .personId(keeperCreateRequest.getPersonId())
                        .build());
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }
}
