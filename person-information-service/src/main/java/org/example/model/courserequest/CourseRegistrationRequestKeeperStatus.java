package org.example.model.courserequest;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_registration_request_keeper_status", schema = "course")
@Data
public class CourseRegistrationRequestKeeperStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestKeeperStatusType status;
}
