package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public Optional<ExplorerGroupsService.ExplorerGroup> findById(Integer groupId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    explorerGroupServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findExplorerGroupById(
                                    ExplorerGroupsService.ExplorerGroupByIdRequest.newBuilder()
                                            .setGroupId(groupId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Map<Integer, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Integer> groupIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorerGroupsByGroupIdIn(
                        ExplorerGroupsService.ExplorerGroupsByGroupIdInRequest.newBuilder()
                                .addAllGroupIds(groupIds)
                                .build()
                ).getGroupByGroupIdMapMap();
    }
}
