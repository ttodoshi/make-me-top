package org.example.repository;

import org.example.dto.course.CourseThemeDto;

import java.util.List;
import java.util.Map;

public interface CourseThemeRepository {
    Map<Integer, CourseThemeDto> findCourseThemesByCourseThemeIdIn(List<Integer> themeIds);

    CourseThemeDto getReferenceById(Integer courseThemeId);
}
