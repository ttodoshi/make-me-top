package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.exception.classes.request.StatusNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatus;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperStatusService {
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    public CourseRegistrationRequestKeeperStatus findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType status) {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status));
    }
}
