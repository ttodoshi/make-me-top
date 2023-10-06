package org.example.service;

import org.example.dto.course.CourseWithRatingDto;

import java.util.List;

public interface CourseService {
    List<CourseWithRatingDto> getCoursesRating(List<Integer> courseIds);
}
