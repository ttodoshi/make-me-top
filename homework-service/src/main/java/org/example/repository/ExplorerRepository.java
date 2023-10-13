package org.example.repository;

import org.example.dto.explorer.ExplorerDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    Optional<ExplorerDto> findById(Integer explorerId);

    Map<Integer, ExplorerDto> findExplorersByExplorerIdIn(List<Integer> explorerIds);

    Boolean existsById(Integer explorerId);
}
