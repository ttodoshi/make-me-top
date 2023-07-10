package org.example.repository;

import org.example.model.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Integer> {
    @Query(value = "SELECT * FROM course.course_theme_progress\n" +
            "JOIN course.course_theme ON course_theme_progress.course_theme_id = course_theme.course_theme_id\n" +
            "WHERE course_theme_progress.progress = 100 AND course_theme_progress.explorer_id = 3\n" +
            "ORDER BY course_theme.course_theme_number DESC\n" +
            "LIMIT 1", nativeQuery = true)
    CourseTheme getCurrentCourseTheme(Integer explorerId);
}
