package org.example.repository;

import java.util.List;
import java.util.Map;

public interface CourseRatingRepository {
    Map<Integer, Double> findCourseRatingsByCourseIdIn(List<Integer> courseIds);
}