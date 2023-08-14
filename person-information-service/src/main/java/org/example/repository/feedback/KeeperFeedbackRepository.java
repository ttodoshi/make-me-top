package org.example.repository.feedback;

import org.example.dto.feedback.KeeperFeedbackDTO;
import org.example.dto.feedback.PersonWithRating;
import org.example.model.feedback.KeeperFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeeperFeedbackRepository extends JpaRepository<KeeperFeedback, Integer> {
    @Query(value = "SELECT AVG(keeper_feedback.rating) FROM course.keeper_feedback\n" +
            "JOIN course.explorer ON explorer.explorer_id = keeper_feedback.explorer_id\n" +
            "JOIN course.person ON person.person_id = explorer.person_id\n" +
            "WHERE person.person_id = ?1\n" +
            "GROUP BY person.person_id", nativeQuery = true)
    Optional<Double> getExplorerRating(Integer personId);

    @Query(value = "SELECT new org.example.dto.feedback.PersonWithRating(\n" +
            "\tp.personId, p.firstName, p.lastName, p.patronymic, COALESCE(ROUND(AVG(kf.rating), 1), 0) as rating\n" +
            ")\n" +
            "FROM Person p\n" +
            "LEFT JOIN Explorer e ON e.personId = p.personId\n" +
            "LEFT JOIN KeeperFeedback kf ON kf.explorerId = e.explorerId\n" +
            "GROUP BY p.personId\n" +
            "ORDER BY rating DESC")
    List<PersonWithRating> getRatingTable();

    @Query(value = "SELECT new org.example.dto.feedback.KeeperFeedbackDTO(" +
            "\tp.personId, p.firstName, p.lastName, p.patronymic, k.keeperId, c.courseId, c.title, kf.rating, kf.comment" +
            ") FROM KeeperFeedback kf\n" +
            "JOIN Keeper k ON k.keeperId = kf.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "JOIN Course c ON c.courseId = k.courseId\n" +
            "JOIN Explorer e ON e.explorerId = kf.explorerId\n" +
            "WHERE e.personId = :personId")
    List<KeeperFeedbackDTO> getExplorerCommentsByPersonId(@Param("personId") Integer personId);
}
