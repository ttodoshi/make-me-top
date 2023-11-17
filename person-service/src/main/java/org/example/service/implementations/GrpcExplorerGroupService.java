package org.example.service.implementations;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.model.ExplorerGroup;
import org.example.service.ExplorerGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcExplorerGroupService extends ExplorerGroupServiceGrpc.ExplorerGroupServiceImplBase {
    private final ExplorerGroupService explorerGroupService;
    private final GrpcExplorerService grpcExplorerService;

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerGroupById(ExplorerGroupsService.ExplorerGroupByIdRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroup> responseObserver) {
        ExplorerGroup group = explorerGroupService.findGroupById(request.getGroupId());
        responseObserver.onNext(mapExplorerGroupToGrpcModel(group));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerGroupsByGroupIdIn(ExplorerGroupsService.ExplorerGroupsByGroupIdInRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroupsByGroupIdInResponse> responseObserver) {
        responseObserver.onNext(ExplorerGroupsService.ExplorerGroupsByGroupIdInResponse
                .newBuilder()
                .putAllGroupByGroupIdMap(
                        explorerGroupService.findExplorerGroupsByGroupIdIn(
                                        request.getGroupIdsList()
                                ).stream()
                                .collect(Collectors.toMap(
                                        ExplorerGroup::getGroupId,
                                        this::mapExplorerGroupToGrpcModel
                                ))
                )
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerGroupsByKeeperIdIn(ExplorerGroupsService.ExplorerGroupsByKeeperIdInRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroupList> responseObserver) {
        responseObserver.onNext(ExplorerGroupsService.ExplorerGroupList
                .newBuilder()
                .addAllGroups(explorerGroupService
                        .findExplorerGroupsByKeeperIdIn(request.getKeeperIdsList())
                        .stream()
                        .map(this::mapExplorerGroupToGrpcModel)
                        .collect(Collectors.toList()))
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) &&" +
            "@roleService.hasAnyCourseRole(#request.courseId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
    @Transactional(readOnly = true)
    public void createGroup(ExplorerGroupsService.CreateGroupRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroup> responseObserver) {
        ExplorerGroup explorerGroup = explorerGroupService.createExplorerGroup(request);
        responseObserver.onNext(mapExplorerGroupToGrpcModel(explorerGroup));
        responseObserver.onCompleted();
    }

    private ExplorerGroupsService.ExplorerGroup mapExplorerGroupToGrpcModel(ExplorerGroup group) {
        return ExplorerGroupsService.ExplorerGroup.newBuilder()
                .setGroupId(group.getGroupId())
                .setCourseId(group.getCourseId())
                .setKeeperId(group.getKeeperId())
                .addAllExplorers(
                        group.getExplorers()
                                .stream()
                                .map(grpcExplorerService::mapExplorerToGrpcModel)
                                .collect(Collectors.toList())
                ).build();
    }
}
