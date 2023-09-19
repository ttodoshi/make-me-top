package org.example.repository;

import org.example.dto.courserequest.CourseRegistrationRequestKeeperDto;

import java.util.List;

public interface CourseRegistrationRequestKeeperRepository {
    List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Integer requestId);

    List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Integer> keeperIds);
}
