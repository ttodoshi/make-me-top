package org.example.person.service.api.courseregistration;

import org.example.person.dto.courserequest.*;
import org.example.person.model.Keeper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseRegistrationRequestService {
    Optional<CourseRegistrationRequestDto> findProcessingCourseRegistrationRequest(String authorizationHeader);

    Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(String authorizationHeader, List<Long> requestIds);

    List<ApprovedRequestDto> getApprovedCourseRegistrationRequests(String authorizationHeader, List<Long> keeperIds);

    List<CourseRegistrationRequestsForKeeperDto> getStudyRequestsForKeeper(String authorizationHeader, List<Keeper> keepers);

    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId(String authorizationHeader);

    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequesForKeepertByExplorerPersonId(String authorizationHeader, Long authenticatedPersonId, Long personId);

    List<GetApprovedCourseRegistrationRequestsForKeeperDto> getApprovedRequestsForKeeper(String authorizationHeader, List<Keeper> keepers);
}
