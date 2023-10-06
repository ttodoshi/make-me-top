package org.example.repository;

import org.example.dto.explorer.ExplorerDto;

import java.util.List;

public interface ExplorerRepository {
    List<ExplorerDto> findExplorersByPersonId(Integer personId);

    ExplorerDto getReferenceById(Integer explorerId);
}
