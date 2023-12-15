package org.example.course.repository;

import org.example.course.dto.mark.CourseMarkDto;

import java.util.Optional;

public interface CourseMarkRepository {
    Optional<CourseMarkDto> findById(Long explorerId);
}
