package org.example.model.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_registration_request_keeper", schema = "course")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseRegistrationRequestKeeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @JoinColumn(table = "course_registration_request", name = "request_id")
    private Integer requestId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime responseDate;
    @JoinColumn(table = "course_registration_request_keeper_status", name = "status_id")
    private Integer statusId;

    public CourseRegistrationRequestKeeper(Integer requestId, Integer keeperId, Integer statusId) {
        this.requestId = requestId;
        this.keeperId = keeperId;
        this.statusId = statusId;
    }
}
