package org.example.feedback.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Long explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    List<ExplorersService.Explorer> findExplorersByPersonId(Long personId);

    Map<Long, ExplorersService.ExplorerList> findExplorersByGroup_CourseIdIn(List<Long> courseIds);

    Map<Long, ExplorersService.ExplorerList> findExplorersByPersonIdIn(List<Long> personIds);
}
