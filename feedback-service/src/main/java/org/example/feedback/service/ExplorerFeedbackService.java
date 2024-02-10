package org.example.feedback.service;

import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.ExplorerFeedbackDto;
import org.example.feedback.dto.offer.CourseRatingOfferDto;
import org.example.feedback.dto.offer.ExplorerFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface ExplorerFeedbackService {
    List<ExplorerFeedbackDto> findExplorerFeedbacksByIdIn(List<Long> feedbackIds);

    Map<Long, ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByKeeperIdIn(List<Long> keeperIds);

    List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);

    List<CourseRatingOfferDto> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);

    Long sendFeedbackForKeeper(String authorizationHeader, Long authenticatedPersonId, CreateExplorerFeedbackDto feedback);

    Long rateCourse(String authorizationHeader, Long authenticatedPersonId, CreateCourseRatingDto feedback);

    Double getRatingByPersonKeeperIds(List<Long> keeperIds);

    ExplorerFeedbackOfferDto closeExplorerFeedbackOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId);

    CourseRatingOfferDto closeCourseRatingOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId);
}
