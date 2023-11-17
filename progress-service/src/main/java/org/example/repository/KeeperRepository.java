package org.example.repository;

import org.example.grpc.KeepersService;

import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    KeepersService.Keeper getReferenceById(Integer keeperId);
}
