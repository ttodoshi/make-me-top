package org.example.repository.homework;

import org.example.model.homework.HomeworkFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkFeedbackRepository extends JpaRepository<HomeworkFeedback, Integer> {
}
