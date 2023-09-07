package org.example.repository.course;

import org.example.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT course_theme.course_id FROM course.course_theme\n" +
            "WHERE course_theme.course_theme_id = ?1", nativeQuery = true)
    Optional<Integer> getCourseIdByThemeId(Integer themeId);
}
