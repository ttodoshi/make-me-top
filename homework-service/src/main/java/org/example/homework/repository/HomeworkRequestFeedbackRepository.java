package org.example.homework.repository;

import org.example.homework.model.HomeworkRequestFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRequestFeedbackRepository extends JpaRepository<HomeworkRequestFeedback, Long> {
    List<HomeworkRequestFeedback> findHomeworkRequestFeedbacksByRequestVersionIdOrderByCreationDateDesc(Long requestVersionId);
}
