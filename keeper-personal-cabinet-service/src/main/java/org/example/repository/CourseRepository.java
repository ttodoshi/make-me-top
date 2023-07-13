package org.example.repository;

import org.example.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT explorer.course_id FROM course.explorer WHERE explorer.explorer_id = ?1", nativeQuery = true)
    Optional<Integer> getCourseIdByExplorerId(Integer explorerId);
}
