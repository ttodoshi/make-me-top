package org.example.service;

import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.feedback.ExplorerCommentDto;
import org.example.dto.feedback.KeeperCommentDto;

import java.util.List;

public interface FeedbackService {
    List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<ExplorerDto> personExplorers);

    List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroupDto> groups);
}
