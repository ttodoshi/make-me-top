package org.example.repository;

import org.example.dto.explorer.ExplorerGroupDto;

public interface ExplorerGroupRepository {
    ExplorerGroupDto getReferenceById(Integer groupId);
}
