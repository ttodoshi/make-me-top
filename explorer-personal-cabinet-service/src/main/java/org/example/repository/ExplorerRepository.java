package org.example.repository;

import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    @Query(value = "SELECT * FROM course.explorer WHERE person_id = ?1 AND course_id = ?2", nativeQuery = true)
    Explorer findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT COUNT(*) FROM course.course_mark\n" +
            "JOIN course.explorer ON explorer.explorer_id = course_mark.explorer_id\n" +
            "WHERE explorer.person_id = ?1", nativeQuery = true)
    Integer getExplorerSystemsCount(Integer personId);
}
