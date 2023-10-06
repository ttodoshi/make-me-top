package org.example.repository;

import org.example.dto.keeper.KeeperDto;

import java.util.List;
import java.util.Map;

public interface KeeperRepository {
    List<KeeperDto> findKeepersByPersonId(Integer personId);

    Map<Integer, KeeperDto> findKeepersByKeeperIdIn(List<Integer> collect);

    KeeperDto getReferenceById(Integer keeperId);
}
