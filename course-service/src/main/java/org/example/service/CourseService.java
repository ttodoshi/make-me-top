package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.dto.course.UpdateCourseDto;
import org.example.dto.event.CourseCreateEvent;
import org.example.dto.explorer.ExplorerWithRatingDto;
import org.example.dto.keeper.CreateKeeperDto;
import org.example.dto.keeper.KeeperWithRatingDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.example.repository.course.CourseRepository;
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
    private final PersonRepository personRepository;

    private final StarSystemService starSystemService;
    private final RatingService ratingService;
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
        List<ExplorerWithRatingDto> explorers = getExplorersForCourse(courseId);
        Collections.sort(explorers);
        List<KeeperWithRatingDto> keepers = getKeepersForCourse(courseId);
        Collections.sort(keepers);
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            explorers.stream()
                    // if person is studying on course
                    .filter(e -> e.getPersonId().equals(authenticatedPersonId))
                    .findAny()
                    .ifPresent(e -> {
                        response.put("you", e);
                        KeeperWithRatingDto yourKeeper = mapper.map(keeperRepository.getKeeperForExplorer(e.getExplorerId()), KeeperWithRatingDto.class);
                        yourKeeper.setRating(ratingService.getKeeperRating(yourKeeper.getPersonId()));
                        response.put("yourKeeper", yourKeeper);
                    });
        }
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    private List<ExplorerWithRatingDto> getExplorersForCourse(Integer courseId) {
        Flux<ExplorerWithRatingDto> fluxExplorers = Flux.fromIterable(explorerRepository.findExplorersByCourseId(courseId))
                .flatMap(e -> Mono.fromCallable(
                                        () -> {
                                            ExplorerWithRatingDto explorer = mapper.map(e, ExplorerWithRatingDto.class);
                                            explorer.setRating(ratingService.getExplorerRating(e.getPersonId()));
                                            return explorer;
                                        }
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                );
        return fluxExplorers.collectList().block();
    }

    private List<KeeperWithRatingDto> getKeepersForCourse(Integer courseId) {
        Flux<KeeperWithRatingDto> fluxKeepers = Flux.fromIterable(keeperRepository.findKeepersByCourseId(courseId))
                .flatMap(k -> Mono.fromCallable(
                                        () -> {
                                            KeeperWithRatingDto keeper = mapper.map(k, KeeperWithRatingDto.class);
                                            keeper.setRating(ratingService.getExplorerRating(k.getPersonId()));
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
        List<Integer> systems = starSystemService.getSystemsByGalaxyId(galaxyId)
                .stream()
                .mapToInt(StarSystemDto::getSystemId)
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

    public Course updateCourse(Integer galaxyId, Integer courseId, UpdateCourseDto course) {
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
    public Keeper setKeeperToCourse(Integer courseId, CreateKeeperDto createKeeperDto) {
        courseValidatorService.validateSetKeeperRequest(courseId, createKeeperDto);
        sendGalaxyCacheRefreshMessage(courseId);
        setDefaultExplorersValue(createKeeperDto.getPersonId());
        return keeperRepository.save(
                Keeper.builder()
                        .courseId(courseId)
                        .personId(createKeeperDto.getPersonId())
                        .build());
    }

    private void setDefaultExplorersValue(Integer personId) {
        personRepository.getReferenceById(personId).setMaxExplorers(3);
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }
}
