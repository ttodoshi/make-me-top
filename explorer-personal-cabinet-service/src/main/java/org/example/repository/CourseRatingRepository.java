package org.example.repository;

import org.example.model.feedback.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Integer> {
}
