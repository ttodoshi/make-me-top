package org.example.homework.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    Optional<KeepersService.Keeper> findById(Long keeperId);

    KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds);
}
