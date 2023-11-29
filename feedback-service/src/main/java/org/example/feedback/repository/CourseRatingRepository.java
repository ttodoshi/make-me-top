package org.example.feedback.repository;

import org.example.feedback.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    @Query("SELECT AVG(cr.rating) FROM CourseRating cr\n" +
            "WHERE cr.explorerId IN :explorerIds")
    Optional<Double> findAvgRatingByExplorerIdIn(@Param("explorerIds") List<Long> explorerIds);
}
