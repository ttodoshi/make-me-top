package org.example.repository.course;

import org.example.model.course.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Integer> {
    List<CourseTheme> findCourseThemesByCourseIdOrderByCourseThemeNumber(Integer courseId);
}
