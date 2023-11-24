package org.example.homework.repository;

import org.example.homework.model.HomeworkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HomeworkRequestRepository extends JpaRepository<HomeworkRequest, Integer> {
    List<HomeworkRequest> findHomeworkRequestsByHomeworkIdIn(List<Integer> homeworkIds);

    Optional<HomeworkRequest> findHomeworkRequestByHomeworkIdAndExplorerId(Integer homeworkId, Integer explorerId);

    @Query("SELECT hr FROM HomeworkRequest hr\n" +
            "JOIN HomeworkRequestStatus hrs ON hrs.statusId = hr.statusId\n" +
            "WHERE hr.explorerId IN :explorerIds AND hrs.status != 'CLOSED'")
    List<HomeworkRequest> findOpenedHomeworkRequestsByExplorerIdIn(@Param("explorerIds") List<Integer> explorerIds);
}
