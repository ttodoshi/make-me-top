package org.example.course.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.course.repository.ExplorerGroupRepository;
import org.example.course.utils.AuthorizationHeaderContextHolder;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupsService.ExplorerGroup getReferenceById(Long groupId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
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
