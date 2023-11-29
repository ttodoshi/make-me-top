package org.example.homework.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Long explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    boolean existsById(Long explorerId);

    Map<Long, ExplorersService.Explorer> findExplorersByExplorerIdIn(List<Long> explorerIds);

    ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds);
}
