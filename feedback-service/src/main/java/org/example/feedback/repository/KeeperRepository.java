package org.example.feedback.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    List<KeepersService.Keeper> findKeepersByPersonId(Long personId);

    Map<Long, KeepersService.KeeperList> findKeepersByPersonIdIn(List<Long> personIds);

    Optional<KeepersService.Keeper> findById(Long keeperId);
}
