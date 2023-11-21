package org.example.course.service;

import org.example.course.dto.progress.CourseWithThemesProgressDto;

public interface CourseProgressService {
    CourseWithThemesProgressDto getCourseProgress(Integer explorerId);
}
