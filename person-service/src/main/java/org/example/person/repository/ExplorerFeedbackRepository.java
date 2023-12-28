package org.example.person.repository;

import org.example.person.dto.feedback.ExplorerFeedbackDto;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface ExplorerFeedbackRepository {
    Map<Long, ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByKeeperIdIn(List<Long> keeperIds);

    List<ExplorerFeedbackDto> findExplorerFeedbacksByIdIn(List<Long> feedbackIds);
}
