package org.example.repository;

import org.example.dto.explorer.ExplorerDto;
import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorerDto> findById(Integer explorerId);

    Optional<ExplorerDto> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds);
}
