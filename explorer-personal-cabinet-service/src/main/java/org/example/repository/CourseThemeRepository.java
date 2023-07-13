package org.example.repository;

import org.example.model.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Integer> {
    List<CourseTheme> findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(Integer courseId);
}
