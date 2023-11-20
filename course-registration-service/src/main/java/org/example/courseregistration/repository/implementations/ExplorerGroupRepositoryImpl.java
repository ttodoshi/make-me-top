package org.example.courseregistration.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.courseregistration.repository.ExplorerGroupRepository;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupsService.ExplorerGroup save(ExplorerGroupsService.CreateGroupRequest explorerGroup) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .createGroup(explorerGroup);
    }

    @Override
    public List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperIdIn(List<Integer> keeperIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorerGroupsByKeeperIdIn(
                        ExplorerGroupsService.ExplorerGroupsByKeeperIdInRequest.newBuilder()
                                .addAllKeeperIds(keeperIds)
                                .build()
                ).getGroupsList();
    }
}
