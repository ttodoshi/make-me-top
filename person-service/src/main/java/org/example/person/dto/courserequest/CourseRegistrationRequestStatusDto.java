package org.example.person.dto.courserequest;

import lombok.Data;

import javax.persistence.*;

@Data
public class CourseRegistrationRequestStatusDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private CourseRegistrationRequestStatusType status;
}
