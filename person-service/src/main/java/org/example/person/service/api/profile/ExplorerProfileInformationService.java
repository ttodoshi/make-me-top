package org.example.person.service.api.profile;

import org.example.person.dto.profile.ExplorerProfileDto;

import java.util.Map;

public interface ExplorerProfileInformationService {
    ExplorerProfileDto getExplorerProfileInformation(String authorizationHeader, Long authenticatedPersonId);
}
