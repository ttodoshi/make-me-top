package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeeperServiceOuterClass;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    public Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .queryParam("courseId", courseId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public List<KeeperDto> findKeepersByPersonId(Integer personId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new PersonNotFoundException(personId)))
                .collectList()
                .block();
    }

    @Override
    public Optional<KeeperDto> findById(Integer keeperId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/{keeperId}/")
                        .build(keeperId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new KeeperNotFoundException(keeperId)))
                .blockOptional();
    }

    @Override
    public KeeperServiceOuterClass.KeepersByKeeperIdInResponse findKeepersByKeeperIdIn(List<Integer> keeperIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return keeperServiceBlockingStub.withCallCredentials(callCredentials)
                .findKeepersByKeeperIdIn(
                        KeeperServiceOuterClass.KeepersByKeeperIdInRequest.newBuilder()
                                .addAllKeeperIds(keeperIds)
                                .build()
                );
    }
}
