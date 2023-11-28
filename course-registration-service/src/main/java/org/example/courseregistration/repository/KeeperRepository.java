package org.example.courseregistration.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Integer personId);

    Map<Integer, KeepersService.Keeper> findKeepersByKeeperIdIn(List<Integer> keeperIds);

    KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds);
}
