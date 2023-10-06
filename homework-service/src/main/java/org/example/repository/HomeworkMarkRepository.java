package org.example.repository;

import org.example.model.HomeworkMark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkMarkRepository extends JpaRepository<HomeworkMark, Integer> {
}
