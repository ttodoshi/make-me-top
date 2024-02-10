package org.example.person.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.person.config.security.RoleService;
import org.example.person.enums.CourseRoleType;
import org.example.person.mapper.ExplorerGroupMapper;
import org.example.person.model.ExplorerGroup;
import org.example.person.service.implementations.ExplorerGroupService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.example.person.mapper.ExplorerGroupMapper.mapExplorerGroupToGrpcModel;

@GrpcService
@RequiredArgsConstructor
public class GrpcExplorerGroupService extends ExplorerGroupServiceGrpc.ExplorerGroupServiceImplBase {
    private final ExplorerGroupService explorerGroupService;
    private final RoleService roleService;

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
    public void findExplorerGroupsByKeeperId(ExplorerGroupsService.ExplorerGroupsByKeeperIdRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroupList> responseObserver) {
        responseObserver.onNext(ExplorerGroupsService.ExplorerGroupList
                .newBuilder()
                .addAllGroups(explorerGroupService
                        .findExplorerGroupsByKeeperId(request.getKeeperId())
                        .stream()
                        .map(ExplorerGroupMapper::mapExplorerGroupToGrpcModel)
                        .collect(Collectors.toList()))
                .build()
        );
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
                                        ExplorerGroupMapper::mapExplorerGroupToGrpcModel
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
                        .map(ExplorerGroupMapper::mapExplorerGroupToGrpcModel)
                        .collect(Collectors.toList()))
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.person.enums.AuthenticationRoleType).KEEPER)")
    @Transactional
    public void createGroup(ExplorerGroupsService.CreateGroupRequest request, StreamObserver<ExplorerGroupsService.ExplorerGroup> responseObserver) {
        if (!roleService.hasAnyCourseRole(
                (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                request.getCourseId(),
                CourseRoleType.KEEPER
        )) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        ExplorerGroup explorerGroup = explorerGroupService.createExplorerGroup(
                "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                request
        );
        responseObserver.onNext(ExplorerGroupMapper.mapExplorerGroupToGrpcModel(explorerGroup));
        responseObserver.onCompleted();
    }
}
