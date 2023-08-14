package org.example.repository.homework;

import org.example.model.homework.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    List<Homework> findAllByCourseThemeId(Integer courseThemeId);

    @Query(value = "SELECT h FROM Homework h\n" +
            "JOIN HomeworkRequest hr ON hr.homeworkId = h.homeworkId\n" +
            "JOIN HomeworkMark hm ON hm.requestId = hr.requestId\n" +
            "WHERE h.courseThemeId = :courseThemeId AND hr.explorerId = :explorerId")
    List<Homework> findAllCompletedByThemeId(@Param("courseThemeId") Integer courseThemeId,
                                             @Param("explorerId") Integer explorerId);
}
