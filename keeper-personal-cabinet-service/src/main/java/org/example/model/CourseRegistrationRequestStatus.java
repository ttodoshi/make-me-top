package org.example.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "course_registration_request_status", schema = "course")
public class CourseRegistrationRequestStatus {
    @Id
    private Integer statusId;
    private String status;
}
