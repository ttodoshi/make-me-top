package org.example.repository.course;

import org.example.dto.course.CourseWithRatingDto;
import org.example.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT new org.example.dto.course.CourseWithRatingDto(\n" +
            "   c.courseId, c.title, COALESCE(ROUND(AVG(cr.rating), 1), 0), k.keeperId\n" +
            ")\n" +
            "FROM Keeper k\n" +
            "JOIN Course c ON c.courseId = k.courseId\n" +
            "LEFT JOIN ExplorerGroup eg ON eg.courseId = c.courseId\n" +
            "LEFT JOIN Explorer e ON e.groupId = eg.groupId\n" +
            "LEFT JOIN CourseRating cr ON cr.explorerId = e.explorerId\n " +
            "WHERE k.personId = :personId\n" +
            "GROUP BY c.courseId, k.keeperId")
    List<CourseWithRatingDto> findCoursesByKeeperPersonId(@Param("personId") Integer personId);
}
