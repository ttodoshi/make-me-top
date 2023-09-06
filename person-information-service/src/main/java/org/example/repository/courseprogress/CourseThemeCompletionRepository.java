package org.example.repository.courseprogress;

import org.example.model.progress.CourseThemeCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseThemeCompletionRepository extends JpaRepository<CourseThemeCompletion, Integer> {
    @Query(value = "SELECT (\n" +
            "            SELECT COUNT(*) as count FROM course.course_theme_completion\n" +
            "            JOIN course.explorer ON explorer.explorer_id = course_theme_completion.explorer_id\n" +
            "            JOIN course.explorer_group ON explorer_group.group_id = explorer.group_id\n" +
            "            WHERE course_theme_completion.explorer_id = ?1 AND explorer_group.course_id = ?2\n" +
            "       ) / CAST(COUNT(*) as float) * 100 as progress\n" +
            "FROM course.course_theme\n" +
            "WHERE course_theme.course_id = ?2",
            nativeQuery = true)
    Double getCourseProgress(Integer explorerId, Integer courseId);

    @Query(value = "SELECT explorer_group.course_id\n" +
            "FROM course.explorer\n" +
            "JOIN course.explorer_group ON explorer_group.group_id = explorer.group_id\n" +
            "WHERE explorer.person_id = ?1 AND explorer.explorer_id NOT IN (\n" +
            "    SELECT course_mark.explorer_id FROM course.course_mark\n" +
            ")\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<Integer> getCurrentInvestigatedCourseId(Integer personId);

    Optional<CourseThemeCompletion> findCourseThemeProgressByExplorerIdAndCourseThemeId(Integer explorerId, Integer courseThemeId);
}
