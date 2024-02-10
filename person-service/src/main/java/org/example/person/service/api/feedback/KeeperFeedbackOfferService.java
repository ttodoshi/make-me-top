package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.offer.KeeperFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface KeeperFeedbackOfferService {
    Map<Long, KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdIn(String authorizationHeader, List<Long> explorerIds);

    List<KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(String authorizationHeader, List<Long> explorerIds);
}
