package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.TeachingInProcessException;
import org.example.exception.classes.requestEX.RequestAlreadyClosedException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestStatus;
import org.example.model.CourseRegistrationRequestStatusType;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.CourseRegistrationRequestStatusRepository;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.KeeperRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestValidatorService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    @Transactional(readOnly = true)
    public void validateRequest(CourseRegistrationRequest request) {
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
    }

    @Transactional(readOnly = true)
    public void validateGetApprovedRequests(Integer personId, Integer courseId) {
        if (keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId).isEmpty())
            throw new KeeperNotFoundException();
    }

    @Transactional(readOnly = true)
    public void validateStartTeachingRequest(Integer personId) {
        List<KeeperDto> personKeepers = keeperRepository.findKeepersByPersonId(personId);
        List<ExplorerDto> keeperExplorers = explorerGroupRepository
                .findExplorerGroupsByKeeperIdIn(
                        personKeepers.stream().map(KeeperDto::getKeeperId).collect(Collectors.toList())
                ).stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toList());
        List<Integer> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                keeperExplorers.stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
        );
        long currentStudyingExplorersCount = keeperExplorers.stream()
                .filter(e ->
                        !explorersWithFinalAssessment.contains(e.getExplorerId()))
                .count();
        if (currentStudyingExplorersCount > 0)
            throw new TeachingInProcessException();
    }

    private List<Integer> getExplorersWithFinalAssessment(List<Integer> explorerIds) {
        List<Integer> explorersWithFinalAssessment = webClientBuilder
                .baseUrl("http://progress-service/api/v1/progress-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorers/completed/")
                        .queryParam("explorerIds", explorerIds)
                        .build()
                ).header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(Integer.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
        return Objects.requireNonNullElse(explorersWithFinalAssessment, Collections.emptyList());
    }
}