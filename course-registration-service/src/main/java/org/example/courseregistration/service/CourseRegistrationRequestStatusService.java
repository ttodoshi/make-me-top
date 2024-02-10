package org.example.courseregistration.service;

import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestStatus;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;

public interface CourseRegistrationRequestStatusService {
    CourseRegistrationRequestStatus findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType status);

    void updateCourseRegistrationRequestStatus(CourseRegistrationRequest request, CourseRegistrationRequestStatusType status);
}
