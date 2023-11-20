package org.example.homework.repository;

import org.example.homework.model.HomeworkFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkFeedbackRepository extends JpaRepository<HomeworkFeedback, Integer> {
    @Query(value = "SELECT hf FROM HomeworkFeedback hf\n" +
            "JOIN HomeworkFeedbackStatus hfs ON hfs.statusId = hf.statusId\n" +
            "WHERE hf.requestId = :requestId AND hfs.status = 'OPENED'")
    List<HomeworkFeedback> findOpenedHomeworkFeedbacksByRequestId(@Param("requestId") Integer requestId);
}
