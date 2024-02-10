package org.example.person.service.api.profile;

import java.util.Map;

public interface KeeperProfileInformationService {
    Map<String, Object> getKeeperProfileInformation(String authorizationHeader, Long authenticatedPersonId);
}
