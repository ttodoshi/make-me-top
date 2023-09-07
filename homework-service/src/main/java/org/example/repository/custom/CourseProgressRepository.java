package org.example.repository.custom;

import org.example.dto.courseprogress.CourseWithThemesProgress;

public interface CourseProgressRepository {
    CourseWithThemesProgress getCourseProgress(Integer courseId);
}
