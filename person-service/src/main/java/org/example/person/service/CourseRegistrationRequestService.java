package org.example.person.service;

import org.example.person.dto.courserequest.GetApprovedCourseRegistrationRequestsForKeeperDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestsForKeeperDto;
import org.example.person.model.Keeper;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestService {
    List<CourseRegistrationRequestsForKeeperDto> getStudyRequestsForKeeper(List<Keeper> keepers);

    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId();

    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByExplorerPersonId(Integer authenticatedPersonId, Integer personId);

    List<GetApprovedCourseRegistrationRequestsForKeeperDto> getApprovedRequestsForKeeper(List<Keeper> keepers);
}
