package org.example.repository;

import org.example.dto.course.CourseDto;

import java.util.Optional;

public interface CourseRepository {
    Optional<CourseDto> findById(Integer courseId);
}
