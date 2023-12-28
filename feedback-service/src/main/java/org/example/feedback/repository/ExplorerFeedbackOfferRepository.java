package org.example.feedback.repository;

import org.example.feedback.model.ExplorerFeedbackOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExplorerFeedbackOfferRepository extends JpaRepository<ExplorerFeedbackOffer, Long> {
    List<ExplorerFeedbackOffer> findExplorerFeedbackOffersByKeeperIdIn(List<Long> keeperIds);

    List<ExplorerFeedbackOffer> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
