package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_registration_request_status", schema = "course")
@Data
public class CourseRegistrationRequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestStatusType status;
}
