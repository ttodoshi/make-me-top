package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.ExplorerFeedbackDto;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface ExplorerFeedbackService {

    List<ExplorerFeedbackDto> findExplorerFeedbacksByIdIn(String authorizationHeader, List<Long> feedbackIds);
}
