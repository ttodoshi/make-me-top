package org.example.progress.repository;

import org.example.progress.model.CourseThemeCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseThemeCompletionRepository extends JpaRepository<CourseThemeCompletion, Long> {
    @Query(value = "SELECT COUNT(*) / CAST(:totalThemesCount as double) * 100 FROM CourseThemeCompletion ctc\n" +
            "WHERE ctc.explorerId = :explorerId")
    Double getCourseProgress(@Param("explorerId") Long explorerId, @Param("totalThemesCount") Integer totalThemesCount);

    Optional<CourseThemeCompletion> findCourseThemeProgressByExplorerIdAndCourseThemeId(Long explorerId, Long courseThemeId);

    List<CourseThemeCompletion> findCourseThemeProgressByExplorerIdAndCourseThemeIdIn(Long explorerId, List<Long> courseThemeIds);

    boolean existsByExplorerIdAndCourseThemeId(Long explorerId, Long themeId);

    void deleteCourseThemeCompletionsByCourseThemeId(Long themeId);

    void deleteCourseThemeCompletionsByExplorerId(Long explorerId);
}
