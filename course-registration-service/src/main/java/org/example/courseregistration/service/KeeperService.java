package org.example.courseregistration.service;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    Boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);

    KeepersService.Keeper findKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(String authorizationHeader, Long personId);

    Map<Long, KeepersService.Keeper> findKeepersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds);

    Map<Long, KeepersService.Keeper> findKeepersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds);
}
