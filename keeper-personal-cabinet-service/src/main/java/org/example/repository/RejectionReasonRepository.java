package org.example.repository;

import org.example.model.courserequest.RejectionReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionReasonRepository extends JpaRepository<RejectionReason, Integer> {
}
