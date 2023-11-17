package org.example.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Integer personId);

    Optional<KeepersService.Keeper> findById(Integer keeperId);

    Map<Integer, KeepersService.Keeper> findKeepersByKeeperIdIn(List<Integer> keeperIds);
}
