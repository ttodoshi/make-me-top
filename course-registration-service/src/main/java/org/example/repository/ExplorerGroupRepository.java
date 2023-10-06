package org.example.repository;

import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.dto.explorer.ExplorerGroupDto;

public interface ExplorerGroupRepository {
    ExplorerGroupDto save(CreateExplorerGroupDto explorerGroup);
}
