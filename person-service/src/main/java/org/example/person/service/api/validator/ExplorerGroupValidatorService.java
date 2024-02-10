package org.example.person.service.api.validator;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupValidatorService {
    void validateCreateExplorerGroupRequest(String authorizationHeader, ExplorerGroupsService.CreateGroupRequest group);
}
