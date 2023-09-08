package org.example.repository.custom;

import org.example.dto.courseprogress.CourseWithThemesProgressDto;

public interface CourseProgressRepository {
    CourseWithThemesProgressDto getCourseProgress(Integer courseId);
}
