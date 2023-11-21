package org.example.courseregistration.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_registration_request_keeper_status")
@Data
public class CourseRegistrationRequestKeeperStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestKeeperStatusType status;
}
