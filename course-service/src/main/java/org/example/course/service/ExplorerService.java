package org.example.course.service;

import org.example.course.dto.explorer.ExplorerWithRatingDto;

import java.util.List;

public interface ExplorerService {
    List<ExplorerWithRatingDto> getExplorersForCourse(Long courseId);
}
