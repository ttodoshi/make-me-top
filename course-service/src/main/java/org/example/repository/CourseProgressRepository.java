package org.example.repository;

import org.example.dto.courseprogress.CourseWithThemesProgress;

public interface CourseProgressRepository {
    CourseWithThemesProgress getCourseProgress(Integer courseId);
}
