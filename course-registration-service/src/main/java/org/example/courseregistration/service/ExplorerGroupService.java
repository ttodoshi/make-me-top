package org.example.courseregistration.service;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;

public interface ExplorerGroupService {
    ExplorerGroupsService.ExplorerGroup save(String authorizationHeader, ExplorerGroupsService.CreateGroupRequest explorerGroup);

    List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperIdIn(String authorizationHeader, List<Long> keeperIds);
}
