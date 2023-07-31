package org.example.repository;

import org.example.model.homework.HomeworkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HomeworkRequestRepository extends JpaRepository<HomeworkRequest, Integer> {
    Optional<HomeworkRequest> findHomeworkRequestByHomeworkIdAndExplorerId(Integer homeworkId, Integer explorerId);

    @Query(value = "SELECT hr FROM HomeworkRequest hr\n" +
            "JOIN Homework h ON h.homeworkId = hr.homeworkId\n" +
            "JOIN HomeworkRequestStatus hrs ON hrs.statusId = hr.statusId\n" +
            "WHERE h.courseThemeId = :themeId AND hrs.status != 'CLOSED'")
    List<HomeworkRequest> findOpenedHomeworkRequestsByThemeId(@Param("themeId") Integer themeId);
}
