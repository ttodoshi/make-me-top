package org.example.repository;

import org.example.model.HomeworkFeedbackStatus;
import org.example.model.HomeworkFeedbackStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkFeedbackStatusRepository extends JpaRepository<HomeworkFeedbackStatus, Integer> {
    Optional<HomeworkFeedbackStatus> findHomeworkFeedbackStatusByStatus(HomeworkFeedbackStatusType status);
}
