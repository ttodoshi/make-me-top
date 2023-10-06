package org.example.service;

import org.example.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.dto.keeper.KeeperDto;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestService {
    List<CourseRegistrationRequestForKeeperDto> getStudyRequestsForKeeper(List<KeeperDto> keepers);

    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId(Integer personId);

    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByExplorerPersonId(Integer authenticatedPersonId, Integer personId);
}
