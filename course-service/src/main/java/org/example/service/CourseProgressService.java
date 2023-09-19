package org.example.service;

import org.example.dto.courseprogress.CourseWithThemesProgressDto;

public interface CourseProgressService {
    CourseWithThemesProgressDto getCourseProgress(Integer explorerId);
}
