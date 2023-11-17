package org.example.repository;

import org.example.dto.event.ExplorerCreateEvent;
import org.example.grpc.ExplorersService;

import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId);

    void save(ExplorerCreateEvent explorer);
}
