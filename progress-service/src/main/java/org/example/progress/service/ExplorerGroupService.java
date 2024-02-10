package org.example.progress.service;

import org.example.grpc.ExplorerGroupsService;
import org.example.progress.dto.group.CurrentKeeperGroupDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupService {
    ExplorerGroupsService.ExplorerGroup findById(String authorizationHeader, Long groupId);

    Map<Long, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(String authorizationHeader, List<Long> groupIds);

    Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader);
}
