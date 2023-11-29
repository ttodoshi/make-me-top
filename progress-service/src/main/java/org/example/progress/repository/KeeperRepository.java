package org.example.progress.repository;

import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId);

    KeepersService.Keeper getReferenceById(Long keeperId);

    List<KeepersService.Keeper> findKeepersByPersonId(Long personId);
}
