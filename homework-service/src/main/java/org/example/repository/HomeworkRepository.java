package org.example.repository;

import org.example.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    List<Homework> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId);

    List<Homework> findAllByHomeworkIdIn(List<Integer> homeworkIds);

    @Query(value = "SELECT h FROM Homework h\n" +
            "JOIN HomeworkRequest hr ON hr.homeworkId = h.homeworkId\n" +
            "JOIN HomeworkMark hm ON hm.requestId = hr.requestId\n" +
            "WHERE h.courseThemeId = :courseThemeId AND h.groupId = :groupId AND hr.explorerId = :explorerId")
    List<Homework> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
            @Param("courseThemeId") Integer courseThemeId,
            @Param("groupId") Integer groupId,
            @Param("explorerId") Integer explorerId
    );
}
