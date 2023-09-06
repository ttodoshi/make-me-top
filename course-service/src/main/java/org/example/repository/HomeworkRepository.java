package org.example.repository;

import org.example.model.homework.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    List<Homework> findHomeworksByGroupId(Integer groupId);
}
