package org.example.service;

import org.example.dto.explorer.ExplorerWithRatingDto;

import java.util.List;

public interface ExplorerService {
    List<ExplorerWithRatingDto> getExplorersForCourse(Integer courseId);
}
