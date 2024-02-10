package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.KeeperFeedbackDto;
import org.example.person.dto.feedback.offer.KeeperFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface KeeperFeedbackService {
    List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(String authorizationHeader, List<Long> feedbackIds);
}
