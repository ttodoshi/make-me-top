package org.example.repository;

import org.example.dto.course.CourseGetResponse;

public interface CourseRepository {
    CourseGetResponse getCourseById(Integer courseId);
}
