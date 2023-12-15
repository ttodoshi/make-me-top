package org.example.course.service;

import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.grpc.KeepersService;

import java.util.Map;

public interface KeeperService {
    Map<Long, KeeperWithRatingDto> getKeepersForCourse(Long courseId);

    KeepersService.Keeper getKeeperForExplorer(Long explorerId);
}
