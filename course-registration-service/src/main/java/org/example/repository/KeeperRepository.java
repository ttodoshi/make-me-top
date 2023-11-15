package org.example.repository;

import org.example.dto.keeper.KeeperDto;
import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeeperDto> findKeepersByPersonId(Integer personId);

    Optional<KeeperDto> findById(Integer keeperId);

    KeepersService.KeepersByKeeperIdInResponse findKeepersByKeeperIdIn(List<Integer> keeperIds);
}
