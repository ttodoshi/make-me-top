package org.example.progress.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.progress.dto.course.CourseDto;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.dto.progress.CourseThemeCompletedDto;
import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.exception.keeper.DifferentKeeperException;
import org.example.progress.exception.mark.ExplorerDoesNotNeedMarkException;
import org.example.progress.exception.progress.HomeworkNotCompletedException;
import org.example.progress.exception.progress.ThemeAlreadyCompletedException;
import org.example.progress.exception.progress.UnexpectedCourseThemeException;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkValidatorService {
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final KeeperService keeperService;
    private final CourseService courseService;
    private final PlanetService planetService;
    private final HomeworkService homeworkService;

    private final WebClient.Builder webClientBuilder;

    public void validateThemesMarksRequest(String authorizationHeader, Long courseId) {
        courseService.findById(authorizationHeader, courseId);
    }

    @Transactional(readOnly = true)
    public void validateCourseMarkRequest(String authorizationHeader, Long authenticatedPersonId, MarkDto courseMark) {
        ExplorersService.Explorer explorer = explorerService.findById(authorizationHeader, courseMark.getExplorerId());

        if (isNotKeeperForThisExplorer(authorizationHeader, authenticatedPersonId, explorer)) {
            log.warn("authenticated person is not keeper for explorer with id {}", explorer.getExplorerId());
            throw new DifferentKeeperException();
        }
        if (!explorerNeedFinalAssessment(authorizationHeader, courseMark.getExplorerId())) {
            log.warn("explorer {} doesn't need course mark", explorer.getExplorerId());
            throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
        }
    }

    private boolean isNotKeeperForThisExplorer(String authorizationHeader, Long authenticatedPersonId, ExplorersService.Explorer explorer) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService
                .findById(authorizationHeader, explorer.getGroupId());
        KeepersService.Keeper keeper = keeperService
                .findById(authorizationHeader, explorerGroup.getKeeperId());

        return !(authenticatedPersonId.equals(keeper.getPersonId()));
    }

    private boolean explorerNeedFinalAssessment(String authorizationHeader, Long explorerId) {
        List<Long> explorerNeededFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/final-assessments/")
                        .queryParam("explorerIds", Collections.singletonList(explorerId))
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get explorers needed final assessment");
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
        if (explorerNeededFinalAssessment == null)
            return false;
        return explorerNeededFinalAssessment.contains(explorerId);
    }

    @Transactional(readOnly = true)
    public void validateThemeMarkRequest(String authorizationHeader, Long authenticatedPersonId, Long themeId, MarkDto mark) {
        ExplorersService.Explorer explorer = explorerService.findById(authorizationHeader, mark.getExplorerId());

        if (isNotKeeperForThisExplorer(authorizationHeader, authenticatedPersonId, explorer)) {
            log.warn("authenticated person is not keeper for explorer with id {}", explorer.getExplorerId());
            throw new DifferentKeeperException();
        }

        Optional<CourseThemeCompletion> courseThemeProgressOptional = courseThemeCompletionRepository
                .findCourseThemeCompletionByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);

        if (courseThemeProgressOptional.isPresent()) {
            log.warn("mark already exists on theme {} for explorer {}", themeId, explorer.getExplorerId());
            throw new ThemeAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        }
        Long currentThemeId = getCurrentCourseThemeDtoId(authorizationHeader, explorer);
        if (!currentThemeId.equals(themeId)) {
            log.warn("theme {} is not current theme for explorer {}", themeId, explorer.getExplorerId());
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        }
        if (homeworkNotCompleted(authorizationHeader, themeId, explorer)) {
            log.warn("explorer {} didn't complete all homework for theme {}", explorer.getExplorerId(), themeId);
            throw new HomeworkNotCompletedException(themeId);
        }
    }

    private Long getCurrentCourseThemeDtoId(String authorizationHeader, ExplorersService.Explorer explorer) {
        List<CourseThemeCompletedDto> themesProgress = getThemesProgress(
                authorizationHeader, explorer
        ).getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private CourseWithThemesProgressDto getThemesProgress(String authorizationHeader, ExplorersService.Explorer explorer) {
        Long courseId = explorerGroupService.findById(
                authorizationHeader, explorer.getGroupId()
        ).getCourseId();
        CourseDto course = courseService.findById(authorizationHeader, courseId);

        List<CourseThemeCompletedDto> themesCompletion = new ArrayList<>();
        for (PlanetDto p : planetService.findPlanetsBySystemId(authorizationHeader, courseId)) {
            Boolean themeCompleted = courseThemeCompletionRepository
                    .findCourseThemeCompletionByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
            themesCompletion.add(
                    new CourseThemeCompletedDto(p.getPlanetId(), p.getPlanetName(), p.getPlanetNumber(), themeCompleted)
            );
        }
        return CourseWithThemesProgressDto.builder()
                .courseId(courseId)
                .title(course.getTitle())
                .themesWithProgress(themesCompletion)
                .build();
    }

    private boolean homeworkNotCompleted(String authorizationHeader, Long themeId, ExplorersService.Explorer explorer) {
        List<HomeworkDto> allHomeworksByThemeId = homeworkService
                .findHomeworksByCourseThemeIdAndGroupId(authorizationHeader, themeId, explorer.getGroupId());
        Map<Long, List<HomeworkDto>> allCompletedHomeworkByThemeId = homeworkService
                .findAllCompletedByCourseThemeIdAndGroupIdForExplorers(
                        authorizationHeader, themeId, explorer.getGroupId(),
                        Collections.singletonList(explorer.getExplorerId())
                );
        return allHomeworksByThemeId.size() != allCompletedHomeworkByThemeId.get(explorer.getExplorerId()).size();
    }
}
