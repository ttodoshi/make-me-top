package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.course.CourseThemeDto;
import org.example.dto.explorer.ExplorerBasicInfoDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.dto.keeper.KeeperBasicInfoDto;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.dto.progress.CurrentCourseProgressDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Person;
import org.example.repository.*;
import org.example.service.CourseProgressService;
import org.springframework.http.HttpStatus;
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
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    @Override
    public Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId) {
        Optional<CurrentCourseProgressDto> currentCourseProgressOptional = Optional.empty();
        Optional<ExplorerDto> currentSystemExplorerOptional = getCurrentSystemExplorer(personId);
        if (currentSystemExplorerOptional.isEmpty())
            return currentCourseProgressOptional;
        ExplorerDto currentSystemExplorer = currentSystemExplorerOptional.get();
        CourseWithThemesProgressDto courseProgress = getCourseProgress(currentSystemExplorer.getExplorerId());
        double progress = getCourseProgressValue(courseProgress);
        Integer currentThemeId = getCurrentCourseThemeId(courseProgress);
        CourseThemeDto currentTheme = courseThemeRepository.getReferenceById(currentThemeId);
        CourseDto currentCourse = courseRepository.getReferenceById(courseProgress.getCourseId());
        KeeperDto keeper = keeperRepository.getReferenceById(
                explorerGroupRepository.getReferenceById(currentSystemExplorer.getGroupId()).getKeeperId()
        );
        Person keeperPerson = personRepository.getReferenceById(keeper.getPersonId());
        KeeperBasicInfoDto keeperInfo = new KeeperBasicInfoDto(
                keeperPerson.getPersonId(),
                keeperPerson.getFirstName(),
                keeperPerson.getLastName(),
                keeperPerson.getPatronymic(),
                keeper.getKeeperId()
        );
        return Optional.of(
                new CurrentCourseProgressDto(
                        currentSystemExplorer.getExplorerId(),
                        currentSystemExplorer.getGroupId(),
                        currentTheme.getCourseThemeId(),
                        currentTheme.getTitle(),
                        currentCourse.getCourseId(),
                        currentCourse.getTitle(),
                        keeperInfo,
                        progress
                )
        );
    }

    private Optional<ExplorerDto> getCurrentSystemExplorer(Integer personId) {
        List<ExplorerDto> personExplorers = explorerRepository.findExplorersByPersonId(personId);
        List<Integer> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                personExplorers.stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
        );
        return personExplorers.stream()
                .filter(e -> !explorersWithFinalAssessment.contains(e.getExplorerId()))
                .findAny();
    }

    private CourseWithThemesProgressDto getCourseProgress(Integer explorerId) {
        return webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseWithThemesProgressDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new ExplorerNotFoundException(explorerId)))
                .block();
    }

    private double getCourseProgressValue(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesWithProgress = courseProgress.getThemesWithProgress();
        int totalThemes = themesWithProgress.size();
        long completedThemes = themesWithProgress.stream().filter(CourseThemeCompletedDto::getCompleted).count();
        return Math.ceil((double) completedThemes / totalThemes * 10) / 10 * 100;
    }

    private Integer getCurrentCourseThemeId(CourseWithThemesProgressDto courseProgress) {
        List<CourseThemeCompletedDto> themesProgress = courseProgress.getThemesWithProgress();
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroupDto> keeperGroups) {
        List<Integer> explorerIds = keeperGroups.stream()
                .flatMap(g -> g.getExplorers().stream())
                .map(ExplorerDto::getExplorerId)
                .collect(Collectors.toList());
        List<Integer> explorerNeededFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/final-assessment/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Integer.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
        if (explorerNeededFinalAssessment == null)
            return Collections.emptyList();
        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                keeperGroups.stream().map(ExplorerGroupDto::getCourseId).collect(Collectors.toList())
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
    public List<Integer> getInvestigatedSystemIds(List<ExplorerDto> personExplorers) {
        List<Integer> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                personExplorers.stream()
                        .map(ExplorerDto::getExplorerId)
                        .collect(Collectors.toList())
        );
        return new ArrayList<>(
                explorerGroupRepository.findExplorerGroupsCourseIdByGroupIdIn(
                        personExplorers.stream().filter(e ->
                                explorersWithFinalAssessment.contains(e.getExplorerId())
                        ).map(ExplorerDto::getGroupId).collect(Collectors.toList())
                ).values()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerBasicInfoDto> getStudyingExplorersByKeeperPersonId(List<ExplorerGroupDto> keeperGroups) {
        List<Integer> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                keeperGroups.stream()
                        .flatMap(g -> g.getExplorers().stream())
                        .map(ExplorerDto::getExplorerId)
                        .collect(Collectors.toList())
        );
        return keeperGroups.stream()
                .flatMap(g -> g.getExplorers()
                        .stream()
                        .filter(e ->
                                !explorersWithFinalAssessment.contains(e.getExplorerId()))
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
                        })
                ).collect(Collectors.toList());
    }

    private List<Integer> getExplorersWithFinalAssessment(List<Integer> explorerIds) {
        List<Integer> explorersWithFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Integer.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
        return Objects.requireNonNullElse(explorersWithFinalAssessment, Collections.emptyList());
    }
}