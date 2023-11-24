package org.example.homework.repository;

import org.example.homework.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    List<Homework> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId);

    @Query("SELECT h FROM Homework h\n" +
            "WHERE h.courseThemeId = :themeId AND h.groupId IN :groupIds AND h.status.status = 'OPENED'")
    List<Homework> findHomeworksByCourseThemeIdAndGroupIdInAndStatus_OpenedStatus(@Param("themeId") Integer themeId, @Param("groupIds") List<Integer> groupIds);

    @Query("SELECT h FROM Homework h\n" +
            "WHERE h.courseThemeId = :themeId AND h.groupId IN :groupIds AND h.status.status = 'CLOSED'")
    List<Homework> findHomeworksByCourseThemeIdAndGroupIdInAndStatus_ClosedStatus(@Param("themeId") Integer themeId, @Param("groupIds") List<Integer> groupIds);

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

    void deleteAllByCourseThemeId(Integer themeId);
}
