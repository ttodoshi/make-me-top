package org.example.repository;

import org.example.dto.explorer.ExplorerGroupDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerGroupRepository {
    ExplorerGroupDto getReferenceById(Integer groupId);
}
