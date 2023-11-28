package org.example.homework.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Integer explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    boolean existsById(Integer explorerId);

    Map<Integer, ExplorersService.Explorer> findExplorersByExplorerIdIn(List<Integer> explorerIds);

    ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds);
}
