package org.example.course.service;

import org.example.course.dto.theme.CourseThemeDto;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.springframework.security.core.Authentication;

public interface CourseThemeService {
    CourseThemeDto findCourseThemeById(String authorizationHeader, Authentication authentication, Long courseThemeId);

    CourseThemeDto updateCourseTheme(Long courseThemeId, UpdateCourseThemeDto courseTheme);

    void deleteCourseTheme(Long courseThemeId);
}
