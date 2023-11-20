package org.example.homework.repository;

import org.example.homework.model.HomeworkMark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkMarkRepository extends JpaRepository<HomeworkMark, Integer> {
}
