package org.example.person.dto.courserequest;

import lombok.Data;

@Data
public class CourseRegistrationRequestKeeperStatusDto {
    private Integer statusId;
    private CourseRegistrationRequestKeeperStatusType status;
}
