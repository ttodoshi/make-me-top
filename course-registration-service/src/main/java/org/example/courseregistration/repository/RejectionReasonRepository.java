package org.example.courseregistration.repository;

import org.example.courseregistration.model.RejectionReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionReasonRepository extends JpaRepository<RejectionReason, Long> {
}
