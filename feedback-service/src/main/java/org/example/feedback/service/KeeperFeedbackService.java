package org.example.feedback.service;

import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.dto.feedback.KeeperFeedbackDto;
import org.example.feedback.dto.offer.KeeperFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface KeeperFeedbackService {
    List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(List<Long> feedbackIds);

    Map<Long, KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdIn(List<Long> explorerIds);

    List<KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);

    Long sendFeedbackForExplorer(String authorizationHeader, Long authenticatedPersonId, CreateKeeperFeedbackDto feedback);

    Double getRatingByPersonExplorerIds(List<Long> explorerIds);

    KeeperFeedbackOfferDto closeKeeperFeedbackOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId);
}
