package org.example.courseregistration.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_registration_request_status")
@Data
public class CourseRegistrationRequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestStatusType status;
}
