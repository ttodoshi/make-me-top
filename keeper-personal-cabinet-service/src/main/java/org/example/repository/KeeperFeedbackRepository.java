package org.example.repository;

import org.example.model.feedback.KeeperFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeeperFeedbackRepository extends JpaRepository<KeeperFeedback, Integer> {
}
