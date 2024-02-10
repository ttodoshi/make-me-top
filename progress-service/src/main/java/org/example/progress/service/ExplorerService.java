package org.example.progress.service;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    ExplorersService.Explorer findById(String authorizationHeader, Long explorerId);

    ExplorersService.Explorer findExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId);

    Map<Long, ExplorersService.Explorer> findExplorersByExplorerIdIn(String authorizationHeader, List<Long> explorerIds);

    Map<Long, ExplorersService.Explorer> findExplorersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds);

    Boolean existsExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId);
}
