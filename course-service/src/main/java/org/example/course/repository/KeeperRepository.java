package org.example.course.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeepersService.Keeper> findKeepersByCourseId(Integer courseId);
}
