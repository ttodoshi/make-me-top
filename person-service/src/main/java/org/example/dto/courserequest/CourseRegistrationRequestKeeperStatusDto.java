package org.example.dto.courserequest;

import lombok.Data;

@Data
public class CourseRegistrationRequestKeeperStatusDto {
    private Integer statusId;
    private CourseRegistrationRequestKeeperStatusType status;
}
