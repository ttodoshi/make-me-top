package org.example.person.mapper;

import org.example.grpc.ExplorerGroupsService;
import org.example.person.model.ExplorerGroup;

import java.util.stream.Collectors;

public class ExplorerGroupMapper {
    public static ExplorerGroupsService.ExplorerGroup mapExplorerGroupToGrpcModel(ExplorerGroup group) {
        return ExplorerGroupsService.ExplorerGroup.newBuilder()
                .setGroupId(group.getGroupId())
                .setCourseId(group.getCourseId())
                .setKeeperId(group.getKeeperId())
                .addAllExplorers(
                        group.getExplorers()
                                .stream()
                                .map(ExplorerMapper::mapExplorerToGrpcModel)
                                .collect(Collectors.toList())
                ).build();
    }
}
