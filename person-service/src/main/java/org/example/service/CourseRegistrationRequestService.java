package org.example.service;

import org.example.dto.courserequest.GetApprovedCourseRegistrationRequestsForKeeperDto;
import org.example.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.dto.courserequest.CourseRegistrationRequestsForKeeperDto;
import org.example.model.Keeper;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestService {
    List<CourseRegistrationRequestsForKeeperDto> getStudyRequestsForKeeper(List<Keeper> keepers);

    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId();

    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByExplorerPersonId(Integer authenticatedPersonId, Integer personId);

    List<GetApprovedCourseRegistrationRequestsForKeeperDto> getApprovedRequestsForKeeper(List<Keeper> keepers);
}
