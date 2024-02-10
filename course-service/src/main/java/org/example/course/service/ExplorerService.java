package org.example.course.service;

import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    ExplorersService.Explorer findById(String authorizationHeader, Long explorerId);

    ExplorersService.Explorer findExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId);

    List<ExplorersService.Explorer> findExplorersByCourseId(String authorizationHeader, Long courseId);

    Boolean existsExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId);

    Map<Long, ExplorerWithRatingDto> getExplorersForCourse(String authorizationHeader, Long courseId);
}
