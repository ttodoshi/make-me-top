package org.example.courseregistration.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.exception.classes.connect.ConnectException;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.progress.TeachingInProcessException;
import org.example.courseregistration.exception.classes.request.RequestAlreadyClosedException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestStatus;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestStatusRepository;
import org.example.courseregistration.repository.ExplorerGroupRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestValidatorService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    @Transactional(readOnly = true)
    public void validateReply(CourseRegistrationRequest request) {
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(request.getRequestId());
    }

    @Transactional(readOnly = true)
    public void validateGetApprovedRequests(Long personId, Map<Long, KeepersService.Keeper> keepers) {
        keepers.values().forEach(k -> {
            if (!(k.getPersonId() == personId))
                throw new DifferentKeeperException();
        });
    }

    @Transactional(readOnly = true)
    public void validateStartTeachingRequest(Long personId) {
        List<KeepersService.Keeper> personKeepers = keeperRepository.findKeepersByPersonId(personId);
        List<ExplorersService.Explorer> keeperExplorers = explorerGroupRepository
                .findExplorerGroupsByKeeperIdIn(
                        personKeepers.stream().map(KeepersService.Keeper::getKeeperId).collect(Collectors.toList())
                ).stream()
                .flatMap(g -> g.getExplorersList().stream())
                .collect(Collectors.toList());
        List<Long> explorersWithFinalAssessment = getExplorersWithFinalAssessment(
                keeperExplorers.stream().map(ExplorersService.Explorer::getExplorerId).collect(Collectors.toList())
        );

        if (keeperExplorers.size() > explorersWithFinalAssessment.size())
            throw new TeachingInProcessException();
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