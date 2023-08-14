package org.example.repository.courseprogress;

import org.example.model.homework.HomeworkMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeworkMarkRepository extends JpaRepository<HomeworkMark, Integer> {
    Optional<HomeworkMark> findHomeworkMarkByRequestId(Integer requestId);
}
