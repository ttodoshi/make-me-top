package org.example.course.service;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupService {
    ExplorerGroupsService.ExplorerGroup findById(String authorizationHeader, Long groupId);
}
