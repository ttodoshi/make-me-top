package org.example.person.service.implementations.progress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerBasicInfoDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.keeper.KeeperBasicInfoDto;
import org.example.person.dto.mark.ThemeMarkDto;
import org.example.person.dto.progress.CourseThemeCompletedDto;
import org.example.person.dto.progress.CourseWithThemesProgressDto;
import org.example.person.dto.progress.CurrentCourseProgressProfileDto;
import org.example.person.dto.progress.CurrentCourseProgressPublicDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.mapper.KeeperMapper;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.homework.HomeworkService;
import org.example.person.service.api.progress.CourseProgressService;
import org.example.person.service.implementations.ExplorerGroupService;
import org.example.person.service.implementations.ExplorerService;
import org.example.person.service.implementations.KeeperService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseProgressServiceImpl implements CourseProgressService {
    private final WebClient.Builder webClientBuilder;

    private final CourseService courseService;
    private final HomeworkService homeworkService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final KeeperService keeperService;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentCourseProgressProfileDto> getCurrentCourseProgressProfile(String authorizationHeader, Long personId) {
        return getCurrentSystemExplorer(authorizationHeader, personId)
                .map(e -> {
                    CourseWithThemesProgressDto courseProgress = getCourseProgress(
                            authorizationHeader, e.getExplorerId()
                    );
                    CourseThemeCompletedDto currentTheme = getCurrentCourseTheme(courseProgress);

                    double progress = getCourseProgressValue(courseProgress);

                    KeeperBasicInfoDto keeperInfo = KeeperMapper.mapKeeperToKeeperBasicInfoDto(
                            e.getGroup().getKeeper()
                    );

                    return new CurrentCourseProgressProfileDto(
                            e.getExplorerId(),
                            currentTheme.getCourseThemeId(), currentTheme.getTitle(),
                            courseProgress.getCourseId(), courseProgress.getTitle(),
                            keeperInfo,
                            e.getGroupId(),
                            progress,
                            homeworkService.findHomeworksByCourseThemeId(
                                            authorizationHeader, currentTheme.getCourseThemeId()
                                    ).stream()
                                    .filter(h -> h.getStatus() == null)
                                    .count()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentCourseProgressPublicDto> getCurrentCourseProgressPublic(String authorizationHeader, Long personId) {
        return getCurrentSystemExplorer(authorizationHeader, personId)
                .map(e -> {
                    CourseWithThemesProgressDto courseProgress = getCourseProgress(
                            authorizationHeader, e.getExplorerId()
                    );
                    CourseThemeCompletedDto currentTheme = getCurrentCourseTheme(courseProgress);

                    KeeperBasicInfoDto keeperInfo = KeeperMapper.mapKeeperToKeeperBasicInfoDto(
                            e.getGroup().getKeeper()
                    );

                    return new CurrentCourseProgressPublicDto(
                            e.getExplorerId(),
                            currentTheme.getCourseThemeId(), currentTheme.getTitle(),
                            courseProgress.getCourseId(), courseProgress.getTitle(),
                            keeperInfo
                    );
                });
    }

    private Optional<Explorer> getCurrentSystemExplorer(String authorizationHeader, Long personId) {
        List<Explorer> personExplorers = explorerService.findExplorersByPersonId(personId);

        Set<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                authorizationHeader,
                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        return personExplorers.stream()
                .filter(e -> !explorersWithFinalAssessment.contains(e.getExplorerId()))
                .findAny();
    }

    private CourseWithThemesProgressDto getCourseProgress(String authorizationHeader, Long explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get course progress by explorer id {}", explorerId);
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("explorer by id {} not found", explorerId);
                            return Mono.error(
                                    new ExplorerNotFoundException(explorerId)
                            );
                        }
                ).block();
    }

    private double getCourseProgressValue(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesWithProgress = courseProgress.getThemesWithProgress();
        int totalThemes = themesWithProgress.size();
        long completedThemes = themesWithProgress.stream().filter(CourseThemeCompletedDto::getCompleted).count();
        return Math.ceil((double) completedThemes / totalThemes * 10) / 10 * 100;
    }

    private CourseThemeCompletedDto getCurrentCourseTheme(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesProgress = courseProgress.getThemesWithProgress();
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet;
        }
        return themesProgress.get(themesProgress.size() - 1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(String authorizationHeader, List<ExplorerGroup> keeperGroups) {
        List<Long> explorerIds = keeperGroups.stream()
                .flatMap(g -> g.getExplorers().stream())
                .map(Explorer::getExplorerId)
                .collect(Collectors.toList());

        List<Long> explorerNeededFinalAssessment = getExplorerIdsNeededFinalAssessment(authorizationHeader, explorerIds);

        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
                keeperGroups.stream().map(ExplorerGroup::getCourseId).collect(Collectors.toList())
        );

        Map<Long, List<ThemeMarkDto>> explorersThemesMarks = getExplorersThemesMarks(authorizationHeader, explorerNeededFinalAssessment);

        return keeperGroups.stream()
                .flatMap(g -> g.getExplorers().stream()
                        .filter(e ->
                                explorerNeededFinalAssessment.contains(e.getExplorerId()))
                        .map(e -> {
                            Person person = e.getPerson();

                            double averageCourseMark = explorersThemesMarks.get(e.getExplorerId())
                                    .stream()
                                    .mapToInt(ThemeMarkDto::getMark)
                                    .average()
                                    .orElse(0.0);

                            return new ExplorerNeededFinalAssessmentDto(
                                    person.getPersonId(), person.getFirstName(),
                                    person.getLastName(), person.getPatronymic(),
                                    g.getCourseId(),
                                    courses.get(g.getCourseId()).getTitle(),
                                    e.getExplorerId(),
                                    Math.ceil(averageCourseMark * 10) / 10

                            );
                        })
                ).collect(Collectors.toList());
    }

    private List<Long> getExplorerIdsNeededFinalAssessment(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/final-assessments/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find explorer needed find assessment by explorer ids");
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collectList()
                .block();
    }

    private Map<Long, List<ThemeMarkDto>> getExplorersThemesMarks(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/themes/marks/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get explorers theme marks by explorer ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, List<ThemeMarkDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).blockLast();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getInvestigatedSystemIds(String authorizationHeader, List<Explorer> personExplorers) {
        Set<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                authorizationHeader,
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
    public Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader, Long authenticatedPersonId) {
        List<Keeper> keepers = keeperService.findKeepersByPersonId(
                authenticatedPersonId
        );
        List<ExplorerGroup> explorerGroups = explorerGroupService.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        return getCurrentGroup(authorizationHeader, explorerGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader, List<ExplorerGroup> keeperGroups) {
        Set<Long> explorersWithFinalAssessment = new HashSet<>(getExplorersWithFinalAssessment(
                authorizationHeader,
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
                    CourseDto course = courseService.findCourseById(authorizationHeader, g.getCourseId());
                    return new CurrentKeeperGroupDto(
                            g.getGroupId(), g.getCourseId(), g.getKeeperId(),
                            course.getTitle(),
                            g.getExplorers()
                                    .stream()
                                    .map(e -> {
                                        Person person = e.getPerson();
                                        return new ExplorerBasicInfoDto(
                                                person.getPersonId(), person.getFirstName(),
                                                person.getLastName(), person.getPatronymic(),
                                                e.getExplorerId(), g.getCourseId(), e.getGroupId()
                                        );
                                    }).collect(Collectors.toList())
                    );
                });
    }

    private Set<Long> getExplorersWithFinalAssessment(String authorizationHeader, List<Long> explorerIds) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get explorers with final assessment by explorer ids");
                    throw new ConnectException();
                })
                .bodyToFlux(Long.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collect(Collectors.toSet())
                .block();
    }
}
