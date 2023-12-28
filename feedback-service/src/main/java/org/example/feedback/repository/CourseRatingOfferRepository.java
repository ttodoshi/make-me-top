package org.example.feedback.repository;

import org.example.feedback.model.CourseRatingOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRatingOfferRepository extends JpaRepository<CourseRatingOffer, Long> {
    List<CourseRatingOffer> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
