package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.exception.courserequest.StatusNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestStatus;
import org.example.courseregistration.model.CourseRegistrationRequestStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestStatusRepository;
import org.example.courseregistration.service.CourseRegistrationRequestStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestStatusServiceImpl implements CourseRegistrationRequestStatusService {
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;

    @Override
    public CourseRegistrationRequestStatus findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType status) {
        return courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(status)
                .orElseThrow(() -> {
                    log.error("status {} not found", status);
                    return new StatusNotFoundException(status);
                });
    }

    @Override
    @Transactional
    public void updateCourseRegistrationRequestStatus(CourseRegistrationRequest request, CourseRegistrationRequestStatusType status) {
        Long statusId = findCourseRegistrationRequestStatusByStatus(status).getStatusId();
        request.setStatusId(statusId);
    }
}
