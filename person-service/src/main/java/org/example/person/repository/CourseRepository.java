package org.example.person.repository;

import org.example.person.dto.course.CourseDto;

import java.util.List;
import java.util.Map;

public interface CourseRepository {
    Map<Integer, CourseDto> findCoursesByCourseIdIn(List<Integer> courseIds);

    CourseDto getReferenceById(Integer courseId);

    Boolean existsById(Integer courseId);
}
