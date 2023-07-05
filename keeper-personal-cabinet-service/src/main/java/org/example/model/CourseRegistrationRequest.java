package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "course_registration_request", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRegistrationRequest {
    @Id
    private Integer requestId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "person", name = "person_id")
    private Integer personId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @CreatedDate
    private Date requestDate;
    @JoinColumn(table = "course_registration_request_status", name = "course_registration_request_status_id")
    private Integer statusId;
}
