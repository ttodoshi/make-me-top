package org.example.repository;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupRepository {
    Optional<ExplorerGroupsService.ExplorerGroup> findById(Integer groupId);

    Map<Integer, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Integer> groupIds);
}
