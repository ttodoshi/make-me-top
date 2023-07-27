package org.example.model.courserequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "keeper_rejection", schema = "course")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeeperRejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @JoinColumn(name = "request_id", table = "course_registration_request")
    private Integer requestId;
    @JoinColumn(name = "reason_id", table = "rejection_reason")
    private Integer reasonId;
}
