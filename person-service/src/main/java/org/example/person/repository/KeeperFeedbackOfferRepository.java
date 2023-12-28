package org.example.person.repository;

import org.example.person.dto.feedback.offer.KeeperFeedbackOfferDto;

import java.util.List;

public interface KeeperFeedbackOfferRepository {
    List<KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
