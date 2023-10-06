package org.example.repository;

import org.example.dto.explorer.ExplorerDto;

import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);
}
