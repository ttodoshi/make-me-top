package org.example.feedback.service;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperService {
    KeepersService.Keeper findById(String authorizationHeader, Long keeperId);

    KeepersService.Keeper findKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Long personId);

    Map<Long, KeepersService.KeeperList> findKeepersByPersonIdIn(List<Long> personIds);
}
