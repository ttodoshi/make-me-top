package org.example.person.service;

import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.dto.feedback.ExplorerCommentDto;
import org.example.person.dto.feedback.KeeperCommentDto;

import java.util.List;

public interface FeedbackService {
    List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<Explorer> personExplorers);

    List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroup> groups);
}
