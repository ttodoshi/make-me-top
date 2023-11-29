package org.example.person.repository;

import org.example.person.dto.courserequest.CourseRegistrationRequestKeeperDto;

import java.util.List;

public interface CourseRegistrationRequestKeeperRepository {
    List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long requestId);

    List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds);
}
