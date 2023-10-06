package org.example.repository;

import org.example.dto.explorer.ExplorerDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorerDto> findById(Integer explorerId);

    Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    Map<Integer, ExplorerDto> findExplorersByExplorerIdIn(List<Integer> explorerIds);
}
