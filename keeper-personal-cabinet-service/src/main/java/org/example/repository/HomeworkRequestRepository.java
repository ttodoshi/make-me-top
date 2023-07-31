package org.example.repository;

import org.example.model.homework.HomeworkRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkRequestRepository extends JpaRepository<HomeworkRequest, Integer> {
    Optional<HomeworkRequest> findHomeworkRequestByHomeworkIdAndExplorerId(Integer homeworkId, Integer explorerId);
}
