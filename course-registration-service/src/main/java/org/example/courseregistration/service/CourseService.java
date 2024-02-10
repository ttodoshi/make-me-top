package org.example.courseregistration.service;

public interface CourseService {
    Boolean existsById(String authorizationHeader, Long courseId);
}
