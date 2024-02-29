package org.example.person.service.api.profile;

import org.example.person.dto.profile.KeeperPublicProfileDto;

public interface KeeperPublicInformationService {
    KeeperPublicProfileDto getKeeperPublicInformation(String authorizationHeader, Long personId);
}
