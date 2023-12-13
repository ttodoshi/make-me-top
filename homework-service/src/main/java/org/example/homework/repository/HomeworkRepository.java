package org.example.homework.repository;

import org.example.homework.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId);

    List<Homework> findHomeworksByCourseThemeIdInAndGroupId(List<Long> themeIds, Long groupId);

    @Query("SELECT h FROM Homework h\n" +
            "WHERE h.courseThemeId = :themeId AND h.groupId IN :groupIds")
    List<Homework> findHomeworksByCourseThemeIdAndGroupIdIn(@Param("themeId") Long themeId, @Param("groupIds") List<Long> groupIds);

    List<Homework> findAllByHomeworkIdIn(List<Long> homeworkIds);

    @Query(value = "SELECT h FROM Homework h\n" +
            "JOIN HomeworkRequest hr ON hr.homeworkId = h.homeworkId\n" +
            "JOIN HomeworkMark hm ON hm.requestId = hr.requestId\n" +
            "WHERE h.courseThemeId = :courseThemeId AND h.groupId = :groupId AND hr.explorerId = :explorerId")
    List<Homework> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
            @Param("courseThemeId") Long courseThemeId,
            @Param("groupId") Long groupId,
            @Param("explorerId") Long explorerId
    );

    void deleteAllByCourseThemeId(Long themeId);
}
