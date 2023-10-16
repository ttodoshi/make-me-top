package org.example.repository;

import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.dto.explorer.ExplorerGroupDto;

import java.util.List;

public interface ExplorerGroupRepository {
    ExplorerGroupDto save(CreateExplorerGroupDto explorerGroup);

    List<ExplorerGroupDto> findExplorerGroupsByKeeperIdIn(List<Integer> keeperIds);
}
