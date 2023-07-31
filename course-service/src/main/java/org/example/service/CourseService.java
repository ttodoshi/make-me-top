package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.config.security.RoleService;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.explorer.ExplorerWithRating;
import org.example.dto.keeper.KeeperCreateRequest;
import org.example.dto.keeper.KeeperWithRating;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.event.CourseCreateEvent;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseAlreadyExistsException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.CourseRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final PersonRepository personRepository;

    private final RoleService roleService;

    private final ModelMapper mapper;

    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Value("${info_app_url}")
    private String INFO_APP_URL;

    public Map<String, Object> getCourse(Integer courseId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("course", courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId)));
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        List<ExplorerWithRating> explorers = getExplorersForCourse(courseId);
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            explorers.stream()
                    // if person is studying on course
                    .filter(e -> e.getPersonId().equals(authenticatedPersonId))
                    .findAny()
                    .ifPresent(e -> {
                        response.put("you", e);
                        KeeperWithRating yourKeeper = mapper.map(keeperRepository.getKeeperForPersonOnCourse(e.getPersonId(), courseId), KeeperWithRating.class);
                        yourKeeper.setRating(getKeeperRating(yourKeeper.getPersonId()));
                        response.put("yourKeeper", yourKeeper);
                    });
        }
        Collections.sort(explorers);
        List<KeeperWithRating> keepers = getKeepersForCourse(courseId);
        Collections.sort(keepers);
        response.put("explorers", explorers);
        response.put("keepers", keepers);
        return response;
    }

    private List<ExplorerWithRating> getExplorersForCourse(Integer courseId) {
        List<ExplorerWithRating> explorers = new LinkedList<>();
        explorerRepository.getExplorersByCourseId(courseId).forEach(
                e -> {
                    ExplorerWithRating explorer = mapper.map(e, ExplorerWithRating.class);
                    explorer.setRating(getExplorerRating(explorer.getPersonId()));
                    explorers.add(explorer);
                }
        );
        return explorers;
    }

    private List<KeeperWithRating> getKeepersForCourse(Integer courseId) {
        List<KeeperWithRating> keepers = new LinkedList<>();
        keeperRepository.getKeepersByCourseId(courseId).forEach(
                k -> {
                    KeeperWithRating keeper = mapper.map(k, KeeperWithRating.class);
                    keeper.setRating(getKeeperRating(keeper.getPersonId()));
                    keepers.add(keeper);
                }
        );
        return keepers;
    }

    private Double getExplorerRating(Integer personId) {
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
                .uri("/explorer/" + personId + "/rating/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(Double.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private Double getKeeperRating(Integer personId) {
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
                .uri("/keeper/" + personId + "/rating/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(Double.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systems = Arrays.stream(getSystemsIdByGalaxyId(galaxyId))
                .mapToInt(StarSystemDTO::getSystemId)
                .boxed().collect(Collectors.toList());
        return courseRepository.findAll().stream().filter(
                c -> systems.contains(c.getCourseId())
        ).collect(Collectors.toList());
    }

    @KafkaListener(topics = "courseTopic", containerFactory = "courseKafkaListenerContainerFactory")
    public void createCourse(CourseCreateEvent course) {
        courseRepository.save(mapper.map(course, Course.class));
    }

    public Course updateCourse(Integer galaxyId, Integer courseId, CourseUpdateRequest course) {
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
        boolean courseTitleExists = Arrays.stream(getSystemsIdByGalaxyId(galaxyId))
                .anyMatch(s -> s.getSystemName().equals(updatedCourse.getTitle()) && !s.getSystemId().equals(courseId));
        if (courseTitleExists)
            throw new CourseAlreadyExistsException(updatedCourse.getTitle());
        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());
        return courseRepository.save(updatedCourse);
    }

    private StarSystemDTO[] getSystemsIdByGalaxyId(Integer galaxyId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("/galaxy/" + galaxyId + "/system/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDTO[].class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    public Keeper setKeeperToCourse(Integer courseId, KeeperCreateRequest keeperCreateRequest) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personRepository.existsById(keeperCreateRequest.getPersonId()))
            throw new PersonNotFoundException();
        return keeperRepository.save(
                Keeper.builder()
                        .courseId(courseId)
                        .personId(keeperCreateRequest.getPersonId())
                        .build());
    }
}
