package org.example.person.repository;

import org.example.person.dto.feedback.offer.CourseRatingOfferDto;

import java.util.List;

public interface CourseRatingOfferRepository {
    List<CourseRatingOfferDto> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
