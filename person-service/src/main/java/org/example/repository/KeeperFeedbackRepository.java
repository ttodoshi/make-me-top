package org.example.repository;

import org.example.dto.feedback.KeeperFeedbackDto;

import java.util.List;

public interface KeeperFeedbackRepository {
    List<KeeperFeedbackDto> findKeeperFeedbacksByExplorerIdIn(List<Integer> explorerIds);
}
