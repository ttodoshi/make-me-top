package org.example.courseregistration.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Long personId);

    Map<Long, KeepersService.Keeper> findKeepersByKeeperIdIn(List<Long> keeperIds);

    KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds);
}
