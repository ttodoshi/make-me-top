package org.example.service;

import org.example.dto.keeper.KeeperWithRatingDto;

import java.util.List;
import java.util.Optional;

public interface KeeperService {
    List<KeeperWithRatingDto> getKeepersForCourse(Integer courseId);

    Optional<KeeperWithRatingDto> getKeeperForExplorer(Integer explorerId, List<KeeperWithRatingDto> keepers);
}
