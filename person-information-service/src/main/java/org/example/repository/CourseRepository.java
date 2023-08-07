package org.example.repository;

import org.example.dto.course.CourseWithRating;
import org.example.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT new org.example.dto.course.CourseWithRating(\n" +
            "\tc.courseId, c.title, COALESCE(ROUND(AVG(cr.rating), 1), 0) AS rating, k.keeperId\n" +
            ")\n" +
            "FROM Keeper k\n" +
            "JOIN Course c ON c.courseId = k.courseId\n" +
            "LEFT JOIN Explorer e ON e.courseId = c.courseId\n" +
            "LEFT JOIN CourseRating cr ON cr.explorerId = e.explorerId\n " +
            "WHERE k.personId = :personId\n" +
            "GROUP BY c.courseId, k.keeperId")
    List<CourseWithRating> findCoursesByKeeperPersonId(@Param("personId") Integer personId);
}
