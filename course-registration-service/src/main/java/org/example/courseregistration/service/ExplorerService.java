package org.example.courseregistration.service;

import org.example.grpc.ExplorersService;
import org.example.person.dto.event.ExplorerCreateEvent;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    List<ExplorersService.Explorer> findExplorersByPersonId(String authorizationHeader, Long personId);

    Map<Long, ExplorersService.Explorer> findExplorersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds);

    void save(ExplorerCreateEvent explorer);
}