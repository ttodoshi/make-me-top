package org.example.repository.homework;

import org.example.model.homework.HomeworkResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkResponseRepository extends JpaRepository<HomeworkResponse, Integer> {
    List<HomeworkResponse> findHomeworkResponsesByRequestId(Integer requestId);
}
