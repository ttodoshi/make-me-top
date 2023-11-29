package org.example.progress.repository;

import org.example.progress.model.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Long> {
    @Query("SELECT cm.explorerId FROM CourseMark cm\n" +
            "WHERE cm.explorerId IN :explorerIds")
    List<Long> findExplorerIdsWithFinalAssessment(@Param("explorerIds") List<Long> explorerIds);
}
