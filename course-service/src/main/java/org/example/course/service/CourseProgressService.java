package org.example.course.service;

import org.example.course.dto.courseprogress.CourseWithThemesProgressDto;

public interface CourseProgressService {
    CourseWithThemesProgressDto getCourseProgress(Integer explorerId);
}
