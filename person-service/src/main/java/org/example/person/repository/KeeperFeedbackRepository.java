package org.example.person.repository;

import org.example.person.dto.feedback.KeeperFeedbackDto;
import org.example.person.dto.feedback.offer.KeeperFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface KeeperFeedbackRepository {
    Map<Long, KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdIn(List<Long> explorerIds);

    List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(List<Long> feedbackIds);
}
