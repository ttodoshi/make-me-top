package org.example.repository;

import org.example.dto.feedback.ExplorerFeedbackDto;

import java.util.List;

public interface ExplorerFeedbackRepository {
    List<ExplorerFeedbackDto> findExplorerFeedbacksByKeeperIdIn(List<Integer> keeperIds);
}
