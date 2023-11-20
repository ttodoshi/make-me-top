package org.example.courseregistration.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private RejectionReason reason;
    @Column(name = "reason_id")
    private Integer reasonId;
    @OneToOne(optional = false)
    @JoinColumn(name = "response_id", nullable = false, insertable = false, updatable = false)
    @MapsId
    @JsonBackReference
    private CourseRegistrationRequestKeeper response;

    public KeeperRejection(CourseRegistrationRequestKeeper response, Integer reasonId) {
        this.response = response;
        this.reasonId = reasonId;
    }
}
