package org.example.repository;

import org.example.dto.course.GetCourseDto;

public interface CourseRepository {
    GetCourseDto getCourseById(Integer courseId);
}
