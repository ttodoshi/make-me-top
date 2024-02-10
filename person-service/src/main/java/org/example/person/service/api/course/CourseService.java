package org.example.person.service.api.course;

import org.example.person.dto.course.CourseDto;
import org.example.person.dto.course.CourseWithRatingDto;

import java.util.List;
import java.util.Map;

public interface CourseService {
    Map<Long, CourseDto> findCoursesByCourseIdIn(String authorizationHeader, List<Long> courseIds);

    CourseDto findCourseById(String authorizationHeader, Long courseId);

    Boolean existsById(String authorizationHeader, Long courseId);

    List<CourseWithRatingDto> getCoursesRating(String authorizationHeader, List<Long> courseIds);
}
