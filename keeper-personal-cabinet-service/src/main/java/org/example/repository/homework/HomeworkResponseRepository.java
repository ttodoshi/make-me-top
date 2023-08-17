package org.example.repository.homework;

import org.example.model.homework.HomeworkResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkResponseRepository extends JpaRepository<HomeworkResponse, Integer> {
    Optional<HomeworkResponse> findHomeworkResponseByRequestId(Integer requestId);
}
