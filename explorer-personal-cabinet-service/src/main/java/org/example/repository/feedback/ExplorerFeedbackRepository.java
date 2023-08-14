package org.example.repository.feedback;

import org.example.model.feedback.ExplorerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplorerFeedbackRepository extends JpaRepository<ExplorerFeedback, Integer> {
}
