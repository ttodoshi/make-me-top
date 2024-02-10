package org.example.feedback.service;

import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    ExplorersService.Explorer findById(String authorizationHeader, Long explorerId);

    List<ExplorersService.Explorer> findExplorersByPersonId(Long personId);

    Map<Long, ExplorersService.ExplorerList> findExplorersByGroup_CourseIdIn(String authorizationHeader, List<Long> courseIds);

    Map<Long, ExplorersService.ExplorerList> findExplorersByPersonIdIn(List<Long> personIds);
}
