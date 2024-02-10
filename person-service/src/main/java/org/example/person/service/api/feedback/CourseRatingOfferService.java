package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.offer.CourseRatingOfferDto;

import java.util.List;

public interface CourseRatingOfferService {
    List<CourseRatingOfferDto> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(String authorizationHeader, List<Long> explorerIds);
}
