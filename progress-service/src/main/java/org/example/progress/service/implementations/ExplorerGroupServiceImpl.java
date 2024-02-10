package org.example.progress.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.progress.dto.group.CurrentKeeperGroupDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.service.ExplorerGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerGroupServiceImpl implements ExplorerGroupService {
    private final WebClient.Builder webClientBuilder;
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupsService.ExplorerGroup findById(String authorizationHeader, Long groupId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorerGroupById(
                        ExplorerGroupsService.ExplorerGroupByIdRequest.newBuilder()
                                .setGroupId(groupId)
                                .build()
                );
    }

    @Override
    public Map<Long, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(String authorizationHeader, List<Long> groupIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorerGroupsByGroupIdIn(
                        ExplorerGroupsService.ExplorerGroupsByGroupIdInRequest.newBuilder()
                                .addAllGroupIds(groupIds)
                                .build()
                ).getGroupByGroupIdMapMap();
    }

    @Override
    public Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader) {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri("groups/current/")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get current group");
                    throw new ConnectException();
                })
                .bodyToMono(CurrentKeeperGroupDto.class)
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
                        error -> Mono.empty()
                ).blockOptional();
    }
}
