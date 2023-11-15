package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.dto.course.CourseDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.homework.HomeworkDto;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.mark.MarkDto;
import org.example.dto.planet.PlanetDto;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.markEX.ExplorerDoesNotNeedMarkException;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.progressEX.HomeworkNotCompletedException;
import org.example.exception.classes.progressEX.ThemeAlreadyCompletedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.model.CourseThemeCompletion;
import org.example.repository.*;
import org.example.service.PersonService;
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
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

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
        ExplorerDto explorer = explorerRepository.findById(courseMark.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(courseMark.getExplorerId()));
        if (isNotKeeperForThisExplorer(explorer))
            throw new DifferentKeeperException();
        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        if (!explorerNeedFinalAssessment(courseMark.getExplorerId()))
            throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
    }

    private boolean isNotKeeperForThisExplorer(ExplorerDto explorer) {
        ExplorerGroupDto explorerGroup = explorerGroupRepository.getReferenceById(explorer.getGroupId());
        PersonDto authenticatedPerson = personService.getAuthenticatedPerson();
        KeeperDto keeper = keeperRepository
                .getReferenceById(explorerGroup.getKeeperId());
        return !authenticatedPerson.getPersonId().equals(keeper.getPersonId());
    }

    private boolean explorerNeedFinalAssessment(Integer explorerId) {
        List<Integer> explorerNeededFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/final-assessments/")
                        .queryParam("explorerIds", Collections.singletonList(explorerId))
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Integer.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
        if (explorerNeededFinalAssessment == null)
            return false;
        return explorerNeededFinalAssessment.contains(explorerId);
    }

    @Transactional(readOnly = true)
    public void validateThemeMarkRequest(Integer themeId, MarkDto mark) {
        ExplorerDto explorer = explorerRepository.findById(mark.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(mark.getExplorerId()));
        if (isNotKeeperForThisExplorer(explorer))
            throw new DifferentKeeperException();
        if (mark.getValue() < 1 || mark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        Optional<CourseThemeCompletion> courseThemeProgressOptional = courseThemeCompletionRepository
                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
        if (courseThemeProgressOptional.isPresent())
            throw new ThemeAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
        Integer currentThemeId = getCurrentCourseThemeDtoId(explorer);
        if (!currentThemeId.equals(themeId))
            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
        if (homeworkNotCompleted(themeId, explorer))
            throw new HomeworkNotCompletedException(themeId);
    }

    private Integer getCurrentCourseThemeDtoId(ExplorerDto explorer) {
        List<CourseThemeCompletedDto> themesProgress = getThemesProgress(explorer).getThemesWithProgress();
        for (CourseThemeCompletedDto theme : themesProgress) {
            if (!theme.getCompleted())
                return theme.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private CourseWithThemesProgressDto getThemesProgress(ExplorerDto explorer) {
        Integer courseId = explorerGroupRepository
                .getReferenceById(explorer.getGroupId()).getCourseId();
        CourseDto course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        List<CourseThemeCompletedDto> themesCompletion = new ArrayList<>();
        for (PlanetDto p : planetRepository.findPlanetsBySystemId(courseId)) {
            Boolean themeCompleted = courseThemeCompletionRepository
                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), p.getPlanetId()).isPresent();
            themesCompletion.add(
                    new CourseThemeCompletedDto(p.getPlanetId(), p.getPlanetName(), themeCompleted)
            );
        }
        return CourseWithThemesProgressDto.builder()
                .courseId(courseId)
                .title(course.getTitle())
                .themesWithProgress(themesCompletion)
                .build();
    }

    private boolean homeworkNotCompleted(Integer themeId, ExplorerDto explorer) {
        List<HomeworkDto> allHomeworksByThemeId = homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupId(themeId, explorer.getGroupId());
        List<HomeworkDto> allCompletedHomeworkByThemeId = homeworkRepository
                .findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
                        themeId, explorer.getGroupId(), explorer.getExplorerId()
                );
        return allHomeworksByThemeId.size() == allCompletedHomeworkByThemeId.size();
    }
}
