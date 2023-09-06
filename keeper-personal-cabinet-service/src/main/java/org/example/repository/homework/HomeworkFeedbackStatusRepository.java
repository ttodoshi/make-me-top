package org.example.repository.homework;

import org.example.model.homework.HomeworkFeedbackStatus;
import org.example.model.homework.HomeworkFeedbackStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkFeedbackStatusRepository extends JpaRepository<HomeworkFeedbackStatus, Integer> {
    Optional<HomeworkFeedbackStatus> findHomeworkFeedbackStatusByStatus(HomeworkFeedbackStatusType status);
}
