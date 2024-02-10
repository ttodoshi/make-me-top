package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courseregistration.exception.courserequest.StatusNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatus;
import org.example.courseregistration.model.CourseRegistrationRequestKeeperStatusType;
import org.example.courseregistration.repository.CourseRegistrationRequestKeeperStatusRepository;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestKeeperStatusServiceImpl implements CourseRegistrationRequestKeeperStatusService {
    private final CourseRegistrationRequestKeeperStatusRepository courseRegistrationRequestKeeperStatusRepository;

    @Override
    public CourseRegistrationRequestKeeperStatus findCourseRegistrationRequestKeeperStatusByStatus(CourseRegistrationRequestKeeperStatusType status) {
        return courseRegistrationRequestKeeperStatusRepository
                .findCourseRegistrationRequestKeeperStatusByStatus(status)
                .orElseThrow(() -> {
                    log.error("status {} not found", status);
                    return new StatusNotFoundException(status);
                });
    }

    @Override
    @Transactional
    public void updateCourseRegistrationRequestKeeperStatus(CourseRegistrationRequestKeeper keeperResponse, CourseRegistrationRequestKeeperStatusType status) {
        Long statusId = findCourseRegistrationRequestKeeperStatusByStatus(status).getStatusId();
        keeperResponse.setStatusId(statusId);
    }
}
