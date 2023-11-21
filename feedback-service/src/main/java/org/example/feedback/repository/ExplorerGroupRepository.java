package org.example.feedback.repository;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup getReferenceById(Integer groupId);
}
