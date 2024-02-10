package org.example.progress.service;

import org.example.grpc.KeepersService;

import java.util.List;

public interface KeeperService {
    KeepersService.Keeper findById(String authorizationHeader, Long keeperId);

    List<KeepersService.Keeper> findKeepersByPersonId(Long personId);

    Boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);
}
