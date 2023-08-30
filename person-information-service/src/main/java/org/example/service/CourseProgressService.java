package org.example.service;

import org.example.dto.courseprogress.CurrentCourseProgressDTO;

import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressDTO> getCurrentCourseProgress(Integer personId);
}
