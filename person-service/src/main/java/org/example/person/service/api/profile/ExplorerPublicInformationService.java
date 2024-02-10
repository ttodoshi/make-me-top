package org.example.person.service.api.profile;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface ExplorerPublicInformationService {
    Map<String, Object> getExplorerPublicInformation(String authorizationHeader, Authentication authentication, Long personId);
}
