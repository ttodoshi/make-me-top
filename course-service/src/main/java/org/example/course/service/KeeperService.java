package org.example.course.service;

import org.example.course.dto.keeper.KeeperWithRatingDto;

import java.util.List;

public interface KeeperService {
    List<KeeperWithRatingDto> getKeepersForCourse(Integer courseId);

    KeeperWithRatingDto getKeeperForExplorer(Integer explorerId, List<KeeperWithRatingDto> keepers);
}
