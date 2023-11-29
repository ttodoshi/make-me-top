package org.example.courseregistration.repository;

public interface CourseRepository {
    Boolean existsById(Long courseId);
}
