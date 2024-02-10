package org.example.homework.service;

import org.example.grpc.ExplorerGroupsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupService {
    ExplorerGroupsService.ExplorerGroup findById(String authorizationHeader, Long groupId);

    Map<Long, ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByGroupIdIn(String authorizationHeader, List<Long> groupIds);

    List<ExplorerGroupsService.ExplorerGroup> findExplorerGroupsByKeeperId(String authorizationHeader, Long keeperId);
}
