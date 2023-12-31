package org.example.courseregistration.repository;

import org.example.courseregistration.model.KeeperRejection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeeperRejectionRepository extends JpaRepository<KeeperRejection, Long> {
    Optional<KeeperRejection> findKeeperRejectionByResponseId(Long responseId);
}
