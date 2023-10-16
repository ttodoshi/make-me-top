package org.example.repository;

import org.example.model.KeeperRejection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeeperRejectionRepository extends JpaRepository<KeeperRejection, Integer> {
    Optional<KeeperRejection> findKeeperRejectionByResponseId(Integer responseId);
}
