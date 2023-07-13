package org.example.model.courserequest;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "keeper_rejection", schema = "course")
@Data
public class KeeperRejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @JoinColumn(name = "request_id", table = "course_registration_request")
    private Integer requestId;
    @JoinColumn(name = "reason_id", table = "rejection_reason")
    private Integer reasonId;
}
