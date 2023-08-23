package org.example.model.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course_registration_request", schema = "course")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseRegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "person", name = "person_id")
    private Integer personId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date requestDate;
    @JoinColumn(table = "course_registration_request_status", name = "status_id")
    private Integer statusId;

    public CourseRegistrationRequest(Integer courseId, Integer personId, Integer statusId) {
        this.courseId = courseId;
        this.personId = personId;
        this.statusId = statusId;
    }
}
