package org.example.person.service.api.validator;

import org.springframework.security.core.Authentication;

public interface ExplorerValidatorService {
    void validateGetExplorersByPersonIdRequest(Long personId);

    void validateGetExplorersByCourseIdRequest(String authorizationHeader, Long courseId);

    void validateDeleteExplorerByIdRequest(String authorizationHeader, Authentication authentication, Long explorerId);
}
