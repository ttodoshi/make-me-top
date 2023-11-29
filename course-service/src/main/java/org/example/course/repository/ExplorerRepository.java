package org.example.course.repository;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findById(Long explorerId);

    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    List<ExplorersService.Explorer> findExplorersByCourseId(Long courseId);
}
