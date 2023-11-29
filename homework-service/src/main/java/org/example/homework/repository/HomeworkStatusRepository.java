package org.example.homework.repository;

import org.example.homework.model.HomeworkStatus;
import org.example.homework.model.HomeworkStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkStatusRepository extends JpaRepository<HomeworkStatus, Long> {
    Optional<HomeworkStatus> findHomeworkStatusByStatus(HomeworkStatusType status);
}
