package org.example.course.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Integer explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    List<ExplorersService.Explorer> findExplorersByCourseId(Integer courseId);
}
