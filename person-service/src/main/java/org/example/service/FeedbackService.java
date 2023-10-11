package org.example.service;

import org.example.model.Explorer;
import org.example.model.ExplorerGroup;
import org.example.dto.feedback.ExplorerCommentDto;
import org.example.dto.feedback.KeeperCommentDto;

import java.util.List;

public interface FeedbackService {
    List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<Explorer> personExplorers);

    List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroup> groups);
}
