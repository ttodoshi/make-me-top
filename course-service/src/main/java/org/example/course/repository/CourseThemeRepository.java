package org.example.course.repository;

import org.example.course.model.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Long> {
    List<CourseTheme> findCourseThemesByCourseIdOrderByCourseThemeNumber(Long courseId);
}
