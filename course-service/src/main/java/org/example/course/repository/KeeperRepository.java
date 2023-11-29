package org.example.course.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    List<KeepersService.Keeper> findKeepersByCourseId(Long courseId);
}
