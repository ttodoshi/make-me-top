package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_registration_request_keeper")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseRegistrationRequestKeeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, updatable = false, insertable = false)
    @JsonBackReference
    private CourseRegistrationRequest request;
    @Column(name = "request_id")
    private Integer requestId;
    @Column(nullable = false)
    private Integer keeperId;
    @LastModifiedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime responseDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private CourseRegistrationRequestKeeperStatus status;
    @Column(name = "status_id")
    private Integer statusId;

    public CourseRegistrationRequestKeeper(Integer requestId, Integer keeperId, Integer statusId) {
        this.requestId = requestId;
        this.keeperId = keeperId;
        this.statusId = statusId;
    }

    public Integer getRequestId() {
        return this.request.getRequestId();
    }
}
