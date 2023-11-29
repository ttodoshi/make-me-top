package org.example.person.repository;

import org.example.person.dto.feedback.KeeperFeedbackDto;

import java.util.List;

public interface KeeperFeedbackRepository {
    List<KeeperFeedbackDto> findKeeperFeedbacksByExplorerIdIn(List<Long> explorerIds);
}
