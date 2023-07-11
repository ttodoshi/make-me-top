package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course_registration_request", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "person", name = "person_id")
    private Integer personId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date requestDate;
    @JoinColumn(table = "course_registration_request_status", name = "status_id")
    private Integer statusId;
}
