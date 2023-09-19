package org.example.repository;

import org.example.dto.progress.CourseWithThemesProgressDto;

public interface CourseProgressRepository {
    CourseWithThemesProgressDto getCourseProgress(Integer explorerId);
}
