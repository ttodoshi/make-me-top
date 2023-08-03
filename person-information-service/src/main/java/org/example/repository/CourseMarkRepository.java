package org.example.repository;

import org.example.dto.course.CourseWithRating;
import org.example.model.progress.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
    @Query(value = "SELECT new org.example.dto.course.CourseWithRating(\n" +
            "        c.courseId, c.title, COALESCE((\n" +
            "                SELECT ROUND(AVG(cr.rating),1) FROM CourseRating cr\n" +
            "                JOIN Explorer e1 ON e1.explorerId = cr.explorerId\n" +
            "                WHERE e1.courseId = c.courseId\n" +
            "        ), 0) as rating, crr.keeperId\n" +
            ")\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Explorer e ON e.personId = crr.personId AND e.courseId = crr.courseId\n" +
            "JOIN CourseMark cm ON cm.explorerId = e.explorerId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'APPROVED'")
    List<CourseWithRating> getInvestigatedSystemsByPersonId(@Param("personId") Integer personId);
}
