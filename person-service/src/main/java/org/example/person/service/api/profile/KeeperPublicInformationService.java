package org.example.person.service.api.profile;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface KeeperPublicInformationService {
    Map<String, Object> getKeeperPublicInformation(String authorizationHeader, Long personId);
}
