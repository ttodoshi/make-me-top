package org.example.repository;

import org.example.model.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemProgressRepository extends JpaRepository<CourseProgress, Integer> {
    @Query(value = "SELECT * FROM course.course_progress WHERE explorer_id = ?1",
            nativeQuery = true)
    CourseProgress getSystemProgress(Integer explorerId);
}
