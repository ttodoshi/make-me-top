package org.example.repository.feedback;

import org.example.model.feedback.KeeperFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeeperFeedbackRepository extends JpaRepository<KeeperFeedback, Integer> {
}
