package org.example.repository.feedback;

import org.example.model.feedback.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Integer> {
}
