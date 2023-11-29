package org.example.course.repository;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupRepository {
    ExplorerGroupsService.ExplorerGroup getReferenceById(Long groupId);
}
