package org.example.homework.service;

import org.example.grpc.KeepersService;

import java.util.List;

public interface KeeperService {
    KeepersService.Keeper findById(String authorizationHeader, Long keeperId);

    KeepersService.Keeper findKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);

    KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds);

    boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);
}
