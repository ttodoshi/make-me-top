package org.example.person.service.api.feedback;

import java.util.List;
import java.util.Map;

public interface CourseRatingService {
    Map<Long, Double> findCourseRatingsByCourseIdIn(String authorizationHeader, List<Long> courseIds);
}
