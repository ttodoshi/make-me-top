package org.example.homework.repository;

import org.example.homework.model.HomeworkRequestStatus;
import org.example.homework.model.HomeworkRequestStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkRequestStatusRepository extends JpaRepository<HomeworkRequestStatus, Long> {
    Optional<HomeworkRequestStatus> findHomeworkRequestStatusByStatus(HomeworkRequestStatusType type);
}
