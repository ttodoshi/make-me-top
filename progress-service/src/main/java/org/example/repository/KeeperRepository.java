package org.example.repository;

import org.example.dto.keeper.KeeperDto;

import java.util.Optional;

public interface KeeperRepository {
    Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    KeeperDto getReferenceById(Integer keeperId);
}
