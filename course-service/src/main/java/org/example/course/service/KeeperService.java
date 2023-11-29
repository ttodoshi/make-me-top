package org.example.course.service;

import org.example.course.dto.keeper.KeeperWithRatingDto;

import java.util.List;

public interface KeeperService {
    List<KeeperWithRatingDto> getKeepersForCourse(Long courseId);

    KeeperWithRatingDto getKeeperForExplorer(Long explorerId, List<KeeperWithRatingDto> keepers);
}
