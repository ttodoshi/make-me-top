package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferDto;

import java.util.List;
import java.util.Map;

public interface ExplorerFeedbackOfferService {
    Map<Long, ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds);

    List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(String authorizationHeader, List<Long> explorerIds);
}
