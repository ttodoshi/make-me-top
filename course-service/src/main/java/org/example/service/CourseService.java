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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final StarSystemRepository starSystemRepository;
    private final RatingRepository ratingRepository;
    private final PersonRepository personRepository;

    private final CourseValidatorService courseValidatorService;
    private final RoleService roleService;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
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
                        KeeperWithRating yourKeeper = mapper.map(keeperRepository.getKeeperForExplorer(e.getExplorerId()), KeeperWithRating.class);
                        yourKeeper.setRating(ratingRepository.getKeeperRating(yourKeeper.getPersonId()));
                        response.put("yourKeeper", yourKeeper);
                    });
        }
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    private List<ExplorerWithRating> getExplorersForCourse(Integer courseId) {
        Flux<ExplorerWithRating> fluxExplorers = Flux.fromIterable(explorerRepository.findExplorersByCourseId(courseId))
                .flatMap(e -> Mono.fromCallable(
                                        () -> {
                                            ExplorerWithRating explorer = mapper.map(e, ExplorerWithRating.class);
                                            explorer.setRating(ratingRepository.getExplorerRating(e.getPersonId()));
                                            return explorer;
                                        }
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                );
        return fluxExplorers.collectList().block();
    }

    private List<KeeperWithRating> getKeepersForCourse(Integer courseId) {
        Flux<KeeperWithRating> fluxKeepers = Flux.fromIterable(keeperRepository.findKeepersByCourseId(courseId))
                .flatMap(k -> Mono.fromCallable(
                                        () -> {
                                            KeeperWithRating keeper = mapper.map(k, KeeperWithRating.class);
                                            keeper.setRating(ratingRepository.getExplorerRating(k.getPersonId()));
                                            return keeper;
                                        }
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                );
        return fluxKeepers.collectList().block();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systems = starSystemRepository.getSystemsByGalaxyId(galaxyId)
                .stream()
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

    @Transactional
    public Keeper setKeeperToCourse(Integer courseId, KeeperCreateRequest keeperCreateRequest) {
        courseValidatorService.validateSetKeeperRequest(courseId, keeperCreateRequest);
        sendGalaxyCacheRefreshMessage(courseId);
        setDefaultExplorersValue(keeperCreateRequest.getPersonId());
        return keeperRepository.save(
                Keeper.builder()
                        .courseId(courseId)
                        .personId(keeperCreateRequest.getPersonId())
                        .build());
    }

    private void setDefaultExplorersValue(Integer personId) {
        personRepository.getReferenceById(personId).setMaxExplorers(3);
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }
}
