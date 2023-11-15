package org.example.repository;

import org.example.model.CourseThemeCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseThemeCompletionRepository extends JpaRepository<CourseThemeCompletion, Integer> {
    @Query(value = "SELECT COUNT(*) / CAST(:totalThemesCount as double) * 100 FROM CourseThemeCompletion ctc\n" +
            "WHERE ctc.explorerId = :explorerId")
    Double getCourseProgress(@Param("explorerId") Integer explorerId, @Param("totalThemesCount") Integer totalThemesCount);

    Optional<CourseThemeCompletion> findCourseThemeProgressByExplorerIdAndCourseThemeId(Integer explorerId, Integer courseThemeId);

    void deleteCourseThemeCompletionsByCourseThemeId(Integer themeId);

    void deleteCourseThemeCompletionsByExplorerId(Integer explorerId);
}
