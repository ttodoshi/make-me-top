package org.example.feedback.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Integer personId);

    Map<Integer, KeepersService.KeeperList> findKeepersByPersonIdIn(List<Integer> personIds);

    Optional<KeepersService.Keeper> findById(Integer keeperId);
}
