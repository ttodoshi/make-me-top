package org.example.repository;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup getReferenceById(Integer groupId);
}
