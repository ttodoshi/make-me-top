package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.dto.explorer.ExplorerDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
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
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/")
                        .queryParam("personId", personId)
                        .queryParam("courseId", courseId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public Optional<ExplorerDto> findById(Integer explorerId) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("explorer/{explorerId}/")
                        .build(explorerId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByPersonIdAndGroupCourseIdIn(
                        ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInRequest
                                .newBuilder()
                                .setPersonId(personId)
                                .addAllCourseIds(courseIds)
                                .build()
                );
    }
}
