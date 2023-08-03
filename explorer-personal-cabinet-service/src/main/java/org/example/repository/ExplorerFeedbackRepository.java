package org.example.repository;

import org.example.model.feedback.ExplorerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplorerFeedbackRepository extends JpaRepository<ExplorerFeedback, Integer> {
}
