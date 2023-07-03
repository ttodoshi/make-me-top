package org.example.repository;

import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    @Query(value = "SELECT * FROM course.explorer WHERE person_id = ?1 AND course_id = ?2", nativeQuery = true)
    Explorer findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId);
}
