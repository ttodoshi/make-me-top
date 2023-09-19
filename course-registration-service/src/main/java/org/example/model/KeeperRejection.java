package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "keeper_rejection")
@Data
@NoArgsConstructor
public class KeeperRejection {
    @Id
    private Integer responseId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "reason_id", nullable = false, insertable = false, updatable = false)
    private RejectionReason reason;
    @Column(name = "reason_id")
    private Integer reasonId;
    @OneToOne(optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    @MapsId
    private CourseRegistrationRequestKeeper response;

    public KeeperRejection(Integer responseId, Integer reasonId) {
        this.responseId = responseId;
        this.reasonId = reasonId;
    }
}
