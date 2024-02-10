package org.example.person.service.api.feedback;

import org.example.person.dto.feedback.ExplorerCommentDto;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.feedback.offer.CourseRatingOfferProfileDto;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferProfileDto;
import org.example.person.dto.feedback.offer.KeeperFeedbackOfferProfileDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;

public interface FeedbackService {
    List<CourseRatingOfferProfileDto> getCourseRatingOffers(String authorizationHeader, List<Long> explorerIds);

    List<ExplorerFeedbackOfferProfileDto> getExplorerFeedbackOffers(String authorizationHeader, List<Long> explorerIds);

    List<KeeperFeedbackOfferProfileDto> getKeeperFeedbackOffers(String authorizationHeader, List<Long> explorerIds);

    List<KeeperCommentDto> getFeedbackForPersonAsExplorer(String authorizationHeader, List<Explorer> personExplorers);

    List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(String authorizationHeader, List<ExplorerGroup> groups);
}
