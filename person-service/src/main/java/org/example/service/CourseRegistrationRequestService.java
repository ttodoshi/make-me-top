package org.example.service;

import org.example.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.model.Keeper;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestService {
    List<CourseRegistrationRequestForKeeperDto> getStudyRequestsForKeeper(List<Keeper> keepers);

    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId();

    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByExplorerPersonId(Integer authenticatedPersonId, Integer personId);
}
