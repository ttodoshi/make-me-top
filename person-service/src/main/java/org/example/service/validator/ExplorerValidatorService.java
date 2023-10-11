package org.example.service.validator;

public interface ExplorerValidatorService {
    void validateGetExplorersByPersonIdRequest(Integer personId);

    void validateGetExplorersByCourseIdRequest(Integer courseId);

    void validateDeleteExplorerByIdRequest(Integer explorerId);
}
