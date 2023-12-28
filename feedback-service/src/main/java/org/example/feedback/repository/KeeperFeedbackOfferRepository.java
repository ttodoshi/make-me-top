package org.example.feedback.repository;

import org.example.feedback.model.KeeperFeedbackOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeeperFeedbackOfferRepository extends JpaRepository<KeeperFeedbackOffer, Long> {
    List<KeeperFeedbackOffer> findKeeperFeedbackOffersByExplorerIdIn(List<Long> explorerIds);

    List<KeeperFeedbackOffer> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds);
}
