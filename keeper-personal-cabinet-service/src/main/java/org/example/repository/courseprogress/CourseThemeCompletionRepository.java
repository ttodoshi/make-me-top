package org.example.repository.courseprogress;

import org.example.model.progress.CourseThemeCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseThemeCompletionRepository extends JpaRepository<CourseThemeCompletion, Integer> {
    Optional<CourseThemeCompletion> findCourseThemeProgressByExplorerIdAndCourseThemeId(Integer explorerId, Integer courseThemeId);
}
