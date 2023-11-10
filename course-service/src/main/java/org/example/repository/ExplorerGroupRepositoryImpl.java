package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupServiceOuterClass;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorerGroupRepositoryImpl implements ExplorerGroupRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupServiceOuterClass.ExplorerGroup getReferenceById(Integer groupId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return explorerGroupServiceBlockingStub.withCallCredentials(callCredentials)
                .findExplorerGroupById(
                        ExplorerGroupServiceOuterClass.ExplorerGroupByIdRequest.newBuilder()
                                .setGroupId(groupId)
                                .build()
                );
    }
}
