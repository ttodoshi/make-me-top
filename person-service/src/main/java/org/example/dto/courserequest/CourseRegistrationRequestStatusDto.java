package org.example.dto.courserequest;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_registration_request_status")
@Data
public class CourseRegistrationRequestStatusDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestStatusType status;
}
