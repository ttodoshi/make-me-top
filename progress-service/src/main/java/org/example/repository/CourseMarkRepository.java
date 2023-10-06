package org.example.repository;

import org.example.model.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
    @Query("SELECT cm.explorerId FROM CourseMark cm\n" +
            "WHERE cm.explorerId IN :explorerIds")
    List<Integer> findExplorerIdsWithFinalAssessment(@Param("explorerIds") List<Integer> explorerIds);
}
