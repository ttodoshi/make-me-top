package org.example.repository;

import org.example.dto.courseprogress.CourseWithThemesProgressDto;

public interface CourseProgressRepository {
    CourseWithThemesProgressDto getCourseProgress(Integer courseId);
}
