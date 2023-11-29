package org.example.person.repository;

import org.example.person.dto.course.CourseDto;

import java.util.List;
import java.util.Map;

public interface CourseRepository {
    Map<Long, CourseDto> findCoursesByCourseIdIn(List<Long> courseIds);

    CourseDto getReferenceById(Long courseId);

    Boolean existsById(Long courseId);
}
