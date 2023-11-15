package org.example.service.implementations;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.ExplorerGroupServiceGrpc;
import org.example.grpc.ExplorerGroupsService;
import org.example.model.ExplorerGroup;
import org.example.service.ExplorerGroupService;
import org.springframework.security.access.prepost.PreAuthorize;

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
}
