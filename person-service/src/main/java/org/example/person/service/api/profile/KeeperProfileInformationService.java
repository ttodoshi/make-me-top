package org.example.person.service.api.profile;

import org.example.person.dto.profile.KeeperProfileDto;

import java.util.Map;

public interface KeeperProfileInformationService {
    KeeperProfileDto getKeeperProfileInformation(String authorizationHeader, Long authenticatedPersonId);
}
