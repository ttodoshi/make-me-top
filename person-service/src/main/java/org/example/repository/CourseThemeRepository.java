package org.example.repository;

import org.example.dto.course.CourseThemeDto;

public interface CourseThemeRepository {
    CourseThemeDto getReferenceById(Integer courseThemeId);
}
