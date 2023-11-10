package org.example.repository;

import org.example.dto.keeper.KeeperDto;
import org.example.grpc.KeeperServiceOuterClass;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeeperDto> findKeepersByPersonId(Integer personId);

    Optional<KeeperDto> findById(Integer keeperId);

    KeeperServiceOuterClass.KeepersByKeeperIdInResponse findKeepersByKeeperIdIn(List<Integer> keeperIds);
}
