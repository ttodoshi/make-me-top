package org.example.person.repository;

import org.example.person.dto.feedback.ExplorerFeedbackDto;

import java.util.List;

public interface ExplorerFeedbackRepository {
    List<ExplorerFeedbackDto> findExplorerFeedbacksByKeeperIdIn(List<Integer> keeperIds);
}
