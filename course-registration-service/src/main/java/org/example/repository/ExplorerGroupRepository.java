package org.example.repository;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup save(ExplorerGroupsService.CreateGroupRequest explorerGroup);

    List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperIdIn(List<Integer> keeperIds);
}
