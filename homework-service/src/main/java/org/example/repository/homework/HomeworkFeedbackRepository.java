package org.example.repository.homework;

import org.example.model.homework.HomeworkFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeworkFeedbackRepository extends JpaRepository<HomeworkFeedback, Integer> {
    @Query(value = "SELECT hf FROM HomeworkFeedback hf\n" +
            "JOIN HomeworkFeedbackStatus hfs ON hfs.statusId = hf.statusId\n" +
            "WHERE hfs.status = 'OPENED'")
    List<HomeworkFeedback> findOpenedHomeworkFeedbacksByRequestId(Integer requestId);
}
