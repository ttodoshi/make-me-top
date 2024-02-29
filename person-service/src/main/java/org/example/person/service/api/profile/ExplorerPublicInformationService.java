package org.example.person.service.api.profile;

import org.example.person.dto.profile.ExplorerPublicProfileDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface ExplorerPublicInformationService {
    ExplorerPublicProfileDto getExplorerPublicInformation(String authorizationHeader, Authentication authentication, Long personId);
}
