package org.example.repository.courseprogress;

import org.example.dto.course.CourseWithRatingDto;
import org.example.model.progress.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
    @Query(value = "SELECT new org.example.dto.course.CourseWithRatingDto(\n" +
            "        c.courseId, c.title, COALESCE((\n" +
            "                SELECT ROUND(AVG(cr.rating),1) FROM CourseRating cr\n" +
            "                JOIN Explorer e1 ON e1.explorerId = cr.explorerId\n" +
            "                JOIN ExplorerGroup eg1 ON e1.groupId = eg1.groupId\n" +
            "                WHERE eg1.courseId = c.courseId\n" +
            "        ), 0), eg.keeperId\n" +
            ")\n" +
            "FROM Explorer e\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN CourseMark cm ON cm.explorerId = e.explorerId\n" +
            "JOIN Course c ON c.courseId = eg.courseId\n" +
            "WHERE e.personId = :personId\n" +
            "ORDER BY cm.courseEndDate DESC")
    List<CourseWithRatingDto> getInvestigatedSystemsByPersonId(@Param("personId") Integer personId);
}
