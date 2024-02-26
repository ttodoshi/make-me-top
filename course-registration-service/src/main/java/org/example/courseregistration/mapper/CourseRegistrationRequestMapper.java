package org.example.courseregistration.mapper;

import org.example.courseregistration.dto.courserequest.ApprovedRequestDto;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;

public class CourseRegistrationRequestMapper {
    public static ApprovedRequestDto mapCourseRegistrationRequestKeeperToApprovedRequestDto(CourseRegistrationRequestKeeper keeperResponse) {
        return new ApprovedRequestDto(
                keeperResponse.getRequestId(),
                keeperResponse.getRequest().getCourseId(),
                keeperResponse.getRequest().getPersonId(),
                keeperResponse.getRequest().getStatusId(),
                keeperResponse.getKeeperId(),
                keeperResponse.getResponseDate()
        );
    }
}
