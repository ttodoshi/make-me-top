package org.example.repository;

import org.example.dto.explorer.ExplorerDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    List<ExplorerDto> findExplorersByPersonId(Integer personId);

    Map<Integer, List<ExplorerDto>> findExplorersByGroup_CourseIdIn(List<Integer> courseIds);

    Map<Integer, List<ExplorerDto>> findExplorersByPersonIdIn(List<Integer> personIds);

    Optional<ExplorerDto> findById(Integer explorerId);
}
