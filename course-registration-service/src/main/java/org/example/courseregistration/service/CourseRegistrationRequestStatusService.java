package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.exception.classes.courserequest.StatusNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequestStatus;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestStatusRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestStatusService {
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;

    public CourseRegistrationRequestStatus findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType status) {
        return courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status));
    }
}
