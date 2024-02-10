package org.example.courseregistration.service.implementations;

import io.grpc.CallCredentials;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.courseregistration.service.ExplorerGroupService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExplorerGroupServiceImpl implements ExplorerGroupService {
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupsService.ExplorerGroup save(String authorizationHeader, ExplorerGroupsService.CreateGroupRequest explorerGroup) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .createGroup(explorerGroup);
    }

    @Override
    public List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperIdIn(String authorizationHeader, List<Long> keeperIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
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
