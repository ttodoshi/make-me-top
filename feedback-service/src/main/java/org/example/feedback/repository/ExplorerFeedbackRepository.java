package org.example.feedback.repository;

import org.example.feedback.model.ExplorerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExplorerFeedbackRepository extends JpaRepository<ExplorerFeedback, Long> {
    @Query(value = "SELECT AVG(ef.rating) FROM ExplorerFeedback ef\n" +
            "JOIN ExplorerFeedbackOffer efo ON efo.explorerId = ef.explorerId\n" +
            "WHERE efo.keeperId IN :personKeeperIds")
    Optional<Double> getPersonRatingAsKeeper(@Param("personKeeperIds") List<Long> personKeeperIds);
}
