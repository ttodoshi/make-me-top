package org.example.progress.repository;

import org.example.progress.dto.course.CourseDto;

import java.util.Optional;

public interface CourseRepository {
    Optional<CourseDto> findById(Long courseId);
}
