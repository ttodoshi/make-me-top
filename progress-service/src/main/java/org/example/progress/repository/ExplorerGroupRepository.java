package org.example.progress.repository;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup getReferenceById(Long groupId);

    Map<Long, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Long> groupIds);
}
