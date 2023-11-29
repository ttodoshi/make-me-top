package org.example.homework.repository;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupRepository {
    Optional<ExplorerGroupsService.ExplorerGroup> findById(Long groupId);

    Map<Long, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Long> groupIds);

    List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperId(Long keeperId);
}
