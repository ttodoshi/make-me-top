package org.example.feedback.service;

import java.util.List;
import java.util.Map;

public interface CourseRatingService {
    Map<Long, Double> getCoursesRating(String authorizationHeader, List<Long> courseIds);
}
