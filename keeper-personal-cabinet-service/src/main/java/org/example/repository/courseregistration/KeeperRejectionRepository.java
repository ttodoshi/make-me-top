package org.example.repository.courseregistration;

import org.example.model.courserequest.KeeperRejection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeeperRejectionRepository extends JpaRepository<KeeperRejection, Integer> {
    Optional<KeeperRejection> findKeeperRejectionByRequestId(Integer requestId);
}