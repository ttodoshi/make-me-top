package org.example.courseregistration.repository;

import org.example.person.dto.event.ExplorerCreateEvent;
import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository {
    Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds);

    List<ExplorersService.Explorer> findExplorersByPersonId(Long personId);

    void save(ExplorerCreateEvent explorer);
}
