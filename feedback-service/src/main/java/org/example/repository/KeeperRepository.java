package org.example.repository;

import org.example.dto.keeper.KeeperDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    List<KeeperDto> findKeepersByPersonId(Integer personId);

    Map<Integer, List<KeeperDto>> findKeepersByPersonIdIn(List<Integer> personIds);
}
