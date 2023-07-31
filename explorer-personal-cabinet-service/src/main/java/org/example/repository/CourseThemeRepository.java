package org.example.repository;

import org.example.model.course.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Integer> {
    List<CourseTheme> findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(Integer courseId);

    @Query(value = "SELECT homework.course_theme_id FROM course.homework\n" +
            "WHERE homework.homework_id = ?1", nativeQuery = true)
    Integer getCourseThemeIdByHomeworkId(Integer homeworkId);
}
