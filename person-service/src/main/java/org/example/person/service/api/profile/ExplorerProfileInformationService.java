package org.example.person.service.api.profile;

import java.util.Map;

public interface ExplorerProfileInformationService {
    Map<String, Object> getExplorerProfileInformation(String authorizationHeader, Long authenticatedPersonId);
}
