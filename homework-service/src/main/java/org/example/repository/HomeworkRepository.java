package org.example.repository;

import org.example.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    List<Homework> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId);

    List<Homework> findAllByHomeworkIdIn(List<Integer> homeworkIds);
}
