package org.example.service;

import org.example.dto.course.GetCourseDto;

public interface CourseService {
    GetCourseDto getCourseById(Integer courseId);
}
