package org.example.repository;

import org.example.grpc.ExplorerGroupServiceOuterClass;

public interface ExplorerGroupRepository {
    ExplorerGroupServiceOuterClass.ExplorerGroup getReferenceById(Integer groupId);
}
