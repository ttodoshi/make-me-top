package org.example.repository;

import org.example.model.progress.CourseThemeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanetProgressRepository extends JpaRepository<CourseThemeProgress, Integer> {
    @Query(value = "SELECT ROUND(CAST((\n" +
            "\tSELECT COUNT(*) as count FROM course.course_theme_progress\n" +
            "\tJOIN course.explorer ON explorer.explorer_id = course_theme_progress.explorer_id\n" +
            "\tWHERE course_theme_progress.progress = 100 AND course_theme_progress.explorer_id = ?1 AND explorer.course_id = ?2\n" +
            ") / CAST(COUNT(*) as float) * 100 as numeric), 1) as progress\n" +
            "FROM course.course_theme\n" +
            "WHERE course_theme.course_id = ?2",
            nativeQuery = true)
    Double getSystemProgress(Integer explorerId, Integer courseId);

    @Query(value = "SELECT explorer.course_id FROM course.explorer\n" +
            "WHERE explorer.person_id = ?1 AND explorer.explorer_id NOT IN (\n" +
            "\tSELECT course_mark.explorer_id FROM course.course_mark\n" +
            ") LIMIT 1", nativeQuery = true)
    Integer getCurrentInvestigatedSystemId(Integer personId);

    Optional<CourseThemeProgress> findCourseThemeProgressByExplorerIdAndCourseThemeId(Integer explorerId, Integer courseThemeId);
}
