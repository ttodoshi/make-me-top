package org.example.feedback.repository;

import org.example.feedback.model.KeeperFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeeperFeedbackRepository extends JpaRepository<KeeperFeedback, Integer> {
    List<KeeperFeedback> findKeeperFeedbacksByExplorerIdIn(List<Integer> explorerIds);

    @Query(value = "SELECT AVG(kf.rating) FROM KeeperFeedback kf\n" +
            "WHERE kf.explorerId IN :personExplorerIds")
    Optional<Double> getPersonRatingAsExplorer(@Param("personExplorerIds") List<Integer> personExplorerIds);
}
