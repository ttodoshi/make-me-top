package org.example.homework.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    Optional<KeepersService.Keeper> findById(Integer keeperId);

    KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds);
}
