package org.example.course.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.course.service.ExplorerGroupService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExplorerGroupServiceImpl implements ExplorerGroupService {
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
}
