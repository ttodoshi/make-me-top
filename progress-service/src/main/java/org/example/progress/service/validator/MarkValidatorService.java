package org.example.progress.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.progress.dto.course.CourseDto;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.dto.progress.CourseThemeCompletedDto;
import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.progress.exception.classes.connect.ConnectException;
import org.example.progress.exception.classes.course.CourseNotFoundException;
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.exception.classes.keeper.DifferentKeeperException;
import org.example.progress.exception.classes.mark.ExplorerDoesNotNeedMarkException;
import org.example.progress.exception.classes.progress.HomeworkNotCompletedException;
import org.example.progress.exception.classes.progress.ThemeAlreadyCompletedException;
import org.example.progress.exception.classes.progress.UnexpectedCourseThemeException;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.*;
import org.example.progress.service.PersonService;
import org.example.progress.utils.AuthorizationHeaderContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarkValidatorService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final PlanetRepository planetRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final HomeworkRepository homeworkRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateCourseMarkRequest(MarkDto courseMark) {
        ExplorersService.Explorer explorer = explorerRepository.findById(courseMark.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(courseMark.getExplorerId()));
        if (isNotKeeperForThisExplorer(explorer))
            throw new DifferentKeeperException();
        if (!explorerNeedFinalAssessment(courseMark.getExplorerId()))
            throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
    }

    private boolean isNotKeeperForThisExplorer(ExplorersService.Explorer explorer) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository
                .getReferenceById(explorer.getGroupId());
        PeopleService.Person authenticatedPerson = personService.getAuthenticatedPerson();
        KeepersService.Keeper keeper = keeperRepository
                .getReferenceById(explorerGroup.getKeeperId());
        return !(authenticatedPerson.getPersonId() == keeper.getPersonId());
    }

    private boolean explorerNeedFinalAssessment(Long explorerId) {
        List<Long> explorerNeededFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/final-assessments/")
                        .queryParam("explorerIds", Collections.singletonList(explorerId))
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
            return false;
        return explorerNeededFinalAssessment.contains(explorerId);
    }

    @Transactional(readOnly = true)
    public void validateThemeMarkRequest(Long themeId, MarkDto mark) {
        ExplorersService.Explorer explorer = explorerRepository.findById(mark.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(mark.getExplorerId()));
        if (isNotKeeperForThisExplorer(explorer))
            throw new DifferentKeeperException();
        Optional<CourseThemeCompletion> courseThemeProgressOptional = courseThemeCompletionRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        if (courseThemeProgressOptional.isPresent())
            throw new ThemeAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        Long currentThemeId = getCurrentCourseThemeDtoId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        if (homeworkNotCompleted(themeId, explorer))
            throw new HomeworkNotCompletedException(themeId);
    }

    private Long getCurrentCourseThemeDtoId(ExplorersService.Explorer explorer) {
        List<CourseThemeCompletedDto> themesProgress = getThemesProgress(explorer).getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private CourseWithThemesProgressDto getThemesProgress(ExplorersService.Explorer explorer) {
        Long courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        CourseDto course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        List<CourseThemeCompletedDto> themesCompletion = new ArrayList<>();
        for (PlanetDto p : planetRepository.findPlanetsBySystemId(courseId)) {
            Boolean themeCompleted = courseThemeCompletionRepository
                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
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

    private boolean homeworkNotCompleted(Long themeId, ExplorersService.Explorer explorer) {
        List<HomeworkDto> allHomeworksByThemeId = homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupId(themeId, explorer.getGroupId());
        List<HomeworkDto> allCompletedHomeworkByThemeId = homeworkRepository
                .findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
                        themeId, explorer.getGroupId(), explorer.getExplorerId()
                );
        return allHomeworksByThemeId.size() == allCompletedHomeworkByThemeId.size();
    }
}
