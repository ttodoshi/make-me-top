package org.example.homework.repository;

import org.example.homework.model.HomeworkRequestVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeworkRequestVersionRepository extends JpaRepository<HomeworkRequestVersion, Integer> {
    List<HomeworkRequestVersion> findHomeworkRequestVersionsByRequestIdOrderByCreationDateDesc(Integer requestId);

    @Query(value = "SELECT * FROM homework_request_version\n" +
            "WHERE homework_request_version.request_id = ?1\n" +
            "ORDER BY homework_request_version.creation_date DESC\n" +
            "LIMIT 1", nativeQuery = true)
    HomeworkRequestVersion findLastHomeworkRequestVersionByRequestId(Integer homeworkRequestId);
}
