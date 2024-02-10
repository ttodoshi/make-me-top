package org.example.homework.service.implementations;

import io.grpc.CallCredentials;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.homework.exception.explorer.ExplorerGroupNotFoundException;
import org.example.homework.service.ExplorerGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExplorerGroupServiceImpl implements ExplorerGroupService {
    @GrpcClient("explorerGroups")
    private ExplorerGroupServiceGrpc.ExplorerGroupServiceBlockingStub explorerGroupServiceBlockingStub;

    @Override
    public ExplorerGroupsService.ExplorerGroup findById(String authorizationHeader, Long groupId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return explorerGroupServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findExplorerGroupById(
                            ExplorerGroupsService.ExplorerGroupByIdRequest.newBuilder()
                                    .setGroupId(groupId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("explorer group by id {} not found", groupId);
            throw new ExplorerGroupNotFoundException(groupId);
        }
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
    public List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperId(String authorizationHeader, Long keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerGroupServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorerGroupsByKeeperId(
                        ExplorerGroupsService.ExplorerGroupsByKeeperIdRequest.newBuilder()
                                .setKeeperId(keeperId)
                                .build()
                ).getGroupsList();
    }
}
