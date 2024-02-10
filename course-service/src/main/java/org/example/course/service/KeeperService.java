package org.example.course.service;

import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    KeepersService.Keeper findById(String authorizationHeader, Long keeperId);

    List<KeepersService.Keeper> findKeepersByCourseId(String authorizationHeader, Long courseId);

    Boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId);

    Map<Long, KeeperWithRatingDto> getKeepersForCourse(String authorizationHeader, Long courseId);

    KeepersService.Keeper getKeeperForExplorer(String authorizationHeader, Long explorerId);
}
