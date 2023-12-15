package org.example.course.service;

import org.example.course.dto.explorer.ExplorerWithRatingDto;

import java.util.Map;

public interface ExplorerService {
    Map<Long, ExplorerWithRatingDto> getExplorersForCourse(Long courseId);
}
