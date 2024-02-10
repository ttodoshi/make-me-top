package org.example.person.service.api.validator;

import org.example.person.dto.keeper.CreateKeeperDto;

public interface KeeperValidatorService {
    void validateKeepersByPersonIdRequest(Long personId);

    void validateKeepersByCourseIdRequest(String authorizationHeader, Long courseId);

    void validateSetKeeperRequest(String authorizationHeader, Long courseId, CreateKeeperDto keeper);
}
