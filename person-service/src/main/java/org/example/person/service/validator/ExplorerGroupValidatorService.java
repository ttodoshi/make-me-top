package org.example.person.service.validator;

import org.example.grpc.ExplorerGroupsService;

public interface ExplorerGroupValidatorService {
    void validateCreateExplorerGroupRequest(ExplorerGroupsService.CreateGroupRequest group);
}
