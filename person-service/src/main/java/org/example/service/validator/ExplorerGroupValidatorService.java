package org.example.service.validator;

import org.example.dto.explorer.CreateExplorerGroupDto;

public interface ExplorerGroupValidatorService {
    void validateCreateExplorerGroupRequest(CreateExplorerGroupDto group);
}
