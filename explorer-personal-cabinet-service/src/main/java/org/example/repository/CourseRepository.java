package org.example.repository;

import org.example.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT course_theme.course_id FROM course.course_theme\n" +
            "WHERE course_theme.course_theme_id = ?1", nativeQuery = true)
    Integer getCourseIdByThemeId(Integer themeId);
}
