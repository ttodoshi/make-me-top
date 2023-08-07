package org.example.repository;

import org.example.model.homework.HomeworkRequestStatus;
import org.example.model.homework.HomeworkRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkRequestStatusRepository extends JpaRepository<HomeworkRequestStatus, Integer> {
    Optional<HomeworkRequestStatus> findHomeworkRequestStatusByStatus(HomeworkRequestStatusType type);
}
