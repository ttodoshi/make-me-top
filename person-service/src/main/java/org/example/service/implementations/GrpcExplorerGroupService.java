package org.example.service.implementations;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.model.ExplorerGroup;
import org.example.service.ExplorerGroupService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcExplorerGroupService extends ExplorerGroupServiceGrpc.ExplorerGroupServiceImplBase {
    private final ExplorerGroupService explorerGroupService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findExplorerGroupById(ExplorerGroupsService.ExplorerGroupByIdRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroup> responseObserver) {
        ExplorerGroup group = explorerGroupService.findGroupById(request.getGroupId());
        responseObserver.onNext(
                ExplorerGroupsService.ExplorerGroup.newBuilder()
                        .setGroupId(group.getGroupId())
                        .setCourseId(group.getCourseId())
                        .setKeeperId(group.getKeeperId())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findExplorerGroupsByKeeperIdIn(ExplorerGroupsService.ExplorerGroupsByKeeperIdInRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroupsByKeeperIdInResponse> responseObserver) {
        responseObserver.onNext(ExplorerGroupsService.ExplorerGroupsByKeeperIdInResponse
                .newBuilder()
                .addAllGroups(explorerGroupService
                        .findGroupsByKeeperIdIn(request.getKeeperIdsList())
                        .stream()
                        .map(g -> ExplorerGroupsService.ExplorerGroup.newBuilder()
                                .setGroupId(g.getGroupId())
                                .setCourseId(g.getCourseId())
                                .setKeeperId(g.getKeeperId())
                                .addAllExplorers(
                                        g.getExplorers()
                                                .stream()
                                                .map(e -> ExplorersService.Explorer
                                                        .newBuilder()
                                                        .setExplorerId(e.getExplorerId())
                                                        .setPersonId(e.getPersonId())
                                                        .setGroupId(e.getGroupId())
                                                        .setStartDate(
                                                                Timestamp.newBuilder()
                                                                        .setSeconds(e.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                                                        .setNanos(e.getStartDate().getNano())
                                                                        .build()
                                                        ).build()
                                                ).collect(Collectors.toList())
                                ).build())
                        .collect(Collectors.toList()))
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createGroup(ExplorerGroupsService.CreateGroupRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroup> responseObserver) {
        ExplorerGroup explorerGroup = explorerGroupService.createExplorerGroup(request);
        responseObserver.onNext(
                ExplorerGroupsService.ExplorerGroup.newBuilder()
                        .setGroupId(explorerGroup.getGroupId())
                        .setCourseId(explorerGroup.getCourseId())
                        .setKeeperId(explorerGroup.getKeeperId())
                        .addAllExplorers(Collections.emptyList())
                        .build()
        );
        responseObserver.onCompleted();
    }
}
