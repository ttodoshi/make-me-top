package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerBasicInfoDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.keeper.KeeperBasicInfoDto;
import org.example.person.dto.planet.PlanetDto;
import org.example.person.dto.progress.CourseThemeCompletedDto;
import org.example.person.dto.progress.CourseWithThemesProgressDto;
import org.example.person.dto.progress.CurrentCourseProgressDto;
import org.example.person.exception.classes.connect.ConnectException;
import org.example.person.exception.classes.explorer.ExplorerNotFoundException;
import org.example.person.exception.classes.planet.PlanetNotFoundException;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.repository.*;
import org.example.person.service.CourseProgressService;
import org.example.person.service.ExplorerGroupService;
import org.example.person.service.ExplorerService;
import org.example.person.service.PersonService;
import org.example.person.utils.AuthorizationHeaderContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final PersonRepository personRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final CourseRepository courseRepository;
    private final PlanetRepository planetRepository;

    private final PersonService personService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Long personId) {
        return getCurrentSystemExplorer(personId)
                .map(e -> {
                    CourseWithThemesProgressDto courseProgress = getCourseProgress(
                            e.getExplorerId()
                    );
                    double progress = getCourseProgressValue(courseProgress);

                    Long currentThemeId = getCurrentCourseThemeId(courseProgress);
                    PlanetDto currentPlanet = planetRepository.findById(currentThemeId)
                            .orElseThrow(() -> new PlanetNotFoundException(currentThemeId));
                    CourseDto currentCourse = courseRepository
                            .getReferenceById(courseProgress.getCourseId());

                    Keeper keeper = keeperRepository.getReferenceById(
                            explorerGroupRepository.getReferenceById(e.getGroupId()).getKeeperId()
                    );
                    Person keeperPerson = personRepository.getReferenceById(keeper.getPersonId());
                    KeeperBasicInfoDto keeperInfo = new KeeperBasicInfoDto(
                            keeperPerson.getPersonId(),
                            keeperPerson.getFirstName(),
                            keeperPerson.getLastName(),
                            keeperPerson.getPatronymic(),
                            keeper.getKeeperId()
                    );

                    return new CurrentCourseProgressDto(
                            e.getExplorerId(),
                            e.getGroupId(),
                            currentPlanet.getPlanetId(),
                            currentPlanet.getPlanetName(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle(),
                            keeperInfo,
                            progress
                    );
                });
    }

    private Optional<Explorer> getCurrentSystemExplorer(Long personId) {
        List<Explorer> personExplorers = explorerService.findExplorersByPersonId(personId);
        List<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        return personExplorers.stream()
                .filter(e -> !explorersWithFinalAssessment.contains(e.getExplorerId()))
                .findAny();
    }

    private CourseWithThemesProgressDto getCourseProgress(Long explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new ExplorerNotFoundException(explorerId)))
                .block();
    }

    private double getCourseProgressValue(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesWithProgress = courseProgress.getThemesWithProgress();
        int totalThemes = themesWithProgress.size();
        long completedThemes = themesWithProgress.stream().filter(CourseThemeCompletedDto::getCompleted).count();
        return Math.ceil((double) completedThemes / totalThemes * 10) / 10 * 100;
    }

    private Long getCurrentCourseThemeId(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesProgress = courseProgress.getThemesWithProgress();
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroup> keeperGroups) {
        List<Long> explorerIds = keeperGroups.stream()
                .flatMap(g -> g.getExplorers().stream())
                .map(Explorer::getExplorerId)
                .collect(Collectors.toList());

        List<Long> explorerNeededFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/final-assessments/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
        if (explorerNeededFinalAssessment == null)
            return Collections.emptyList();

        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                keeperGroups.stream().map(ExplorerGroup::getCourseId).collect(Collectors.toList())
        );

        return keeperGroups.stream()
                .flatMap(g -> g.getExplorers().stream()
                        .filter(e ->
                                explorerNeededFinalAssessment.contains(e.getExplorerId()))
                        .map(e -> {
                            Person person = personRepository.getReferenceById(e.getPersonId());
                            return new ExplorerNeededFinalAssessmentDto(
                                    person.getPersonId(),
                                    person.getFirstName(),
                                    person.getLastName(),
                                    person.getPatronymic(),
                                    g.getCourseId(),
                                    courses.get(g.getCourseId()).getTitle(),
                                    e.getExplorerId()
                            );
                        })
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getInvestigatedSystemIds(List<Explorer> personExplorers) {
        List<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                personExplorers.stream()
                        .map(Explorer::getExplorerId)
                        .collect(Collectors.toList())
        );
        return explorerGroupService.findExplorerGroupsByGroupIdIn(
                        personExplorers.stream().filter(e ->
                                explorersWithFinalAssessment.contains(e.getExplorerId())
                        ).map(Explorer::getGroupId).collect(Collectors.toList())
                ).stream()
                .map(ExplorerGroup::getCourseId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentKeeperGroupDto> getCurrentGroup() {
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(
                personService.getAuthenticatedPersonId()
        );
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        return getCurrentGroup(explorerGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentKeeperGroupDto> getCurrentGroup(List<ExplorerGroup> keeperGroups) {
        Set<Long> explorersWithFinalAssessment = new HashSet<>(getExplorersWithFinalAssessment(
                keeperGroups.stream()
                        .flatMap(g -> g.getExplorers().stream())
                        .map(Explorer::getExplorerId)
                        .collect(Collectors.toList())
        ));
        return keeperGroups
                .stream()
                .filter(g -> {
                    List<Long> explorerIds = g.getExplorers().stream().map(Explorer::getExplorerId).collect(Collectors.toList());
                    return !explorersWithFinalAssessment.containsAll(explorerIds);
                }).findAny()
                .map(g -> {
                    CourseDto course = courseRepository.getReferenceById(g.getCourseId());
                    return new CurrentKeeperGroupDto(
                            g.getGroupId(),
                            g.getCourseId(),
                            g.getKeeperId(),
                            course.getTitle(),
                            g.getExplorers()
                                    .stream()
                                    .map(e -> {
                                        Person person = personRepository.getReferenceById(e.getPersonId());
                                        return new ExplorerBasicInfoDto(
                                                person.getPersonId(),
                                                person.getFirstName(),
                                                person.getLastName(),
                                                person.getPatronymic(),
                                                e.getExplorerId(),
                                                g.getCourseId(),
                                                e.getGroupId()
                                        );
                                    }).collect(Collectors.toList())
                    );
                });
    }

    private List<Long> getExplorersWithFinalAssessment(List<Long> explorerIds) {
        List<Long> explorersWithFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
        return Objects.requireNonNullElse(explorersWithFinalAssessment, Collections.emptyList());
    }
}
