package org.example.progress.repository;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup getReferenceById(Integer groupId);

    Map<Integer, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Integer> groupIds);
}
