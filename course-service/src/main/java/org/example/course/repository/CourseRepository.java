package org.example.course.repository;

import org.example.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findCoursesByCourseIdIn(List<Integer> courseIds);
}
