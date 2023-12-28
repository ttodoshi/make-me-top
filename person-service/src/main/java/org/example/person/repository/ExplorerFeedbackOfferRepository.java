package org.example.person.repository;

import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferDto;

import java.util.List;

public interface ExplorerFeedbackOfferRepository {
    List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
