package org.example.person.service;

import org.example.person.dto.course.CourseWithRatingDto;

import java.util.List;

public interface CourseService {
    List<CourseWithRatingDto> getCoursesRating(List<Long> courseIds);
}
