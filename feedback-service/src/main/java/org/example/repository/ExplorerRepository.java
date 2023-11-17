package org.example.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Integer explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    List<ExplorersService.Explorer> findExplorersByPersonId(Integer personId);

    Map<Integer, ExplorersService.ExplorerList> findExplorersByGroup_CourseIdIn(List<Integer> courseIds);

    Map<Integer, ExplorersService.ExplorerList> findExplorersByPersonIdIn(List<Integer> personIds);
}
