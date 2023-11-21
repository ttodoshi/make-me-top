package org.example.person.repository;

import org.example.person.dto.course.CourseThemeDto;

public interface CourseThemeRepository {
    CourseThemeDto getReferenceById(Integer courseThemeId);
}
