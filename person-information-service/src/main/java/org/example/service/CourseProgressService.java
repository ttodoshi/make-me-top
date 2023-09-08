package org.example.service;

import org.example.dto.courseprogress.CurrentCourseProgressDto;

import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId);
}
