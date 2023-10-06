package org.example.repository;

import org.example.dto.course.CourseThemeDto;

import java.util.Optional;

public interface CourseThemeRepository {
    Optional<CourseThemeDto> findById(Integer themeId);

    Boolean existsById(Integer themeId);
}
