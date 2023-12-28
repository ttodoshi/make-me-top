package org.example.person.service;

import org.example.person.dto.feedback.ExplorerCommentDto;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.feedback.offer.CourseRatingOfferProfileDto;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferProfileDto;
import org.example.person.dto.feedback.offer.KeeperFeedbackOfferProfileDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;

public interface FeedbackService {
    List<CourseRatingOfferProfileDto> getCourseRatingOffers(List<Long> explorerIds);

    List<ExplorerFeedbackOfferProfileDto> getExplorerFeedbackOffers(List<Long> explorerIds);

    List<KeeperFeedbackOfferProfileDto> getKeeperFeedbackOffers(List<Long> explorerIds);

    List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<Explorer> personExplorers);

    List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroup> groups);
}
