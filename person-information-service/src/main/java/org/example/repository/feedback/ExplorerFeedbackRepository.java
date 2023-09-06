package org.example.repository.feedback;

import org.example.dto.feedback.ExplorerFeedbackDTO;
import org.example.model.feedback.ExplorerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExplorerFeedbackRepository extends JpaRepository<ExplorerFeedback, Integer> {
    @Query(value = "SELECT AVG(explorer_feedback.rating) FROM course.explorer_feedback\n" +
            "JOIN course.keeper ON keeper.keeper_id = explorer_feedback.keeper_id\n" +
            "JOIN course.person ON person.person_id = keeper.person_id\n" +
            "WHERE person.person_id = ?1\n" +
            "GROUP BY person.person_id", nativeQuery = true)
    Optional<Double> getKeeperRating(Integer personId);

    @Query(value = "SELECT new org.example.dto.feedback.ExplorerFeedbackDTO(\n" +
            "   p.personId, p.firstName, p.lastName, p.patronymic, e.explorerId, c.courseId, c.title, ef.rating, ef.comment\n" +
            ") FROM ExplorerFeedback ef\n" +
            "JOIN Explorer e ON e.explorerId = ef.explorerId\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Course c ON c.courseId = eg.courseId\n" +
            "JOIN Keeper k ON k.keeperId = ef.keeperId\n" +
            "WHERE k.personId = :personId")
    List<ExplorerFeedbackDTO> getKeeperCommentsByPersonId(@Param("personId") Integer personId);
}
