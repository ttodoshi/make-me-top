package org.example.person.service.validator;

public interface ExplorerValidatorService {
    void validateGetExplorersByPersonIdRequest(Long personId);

    void validateGetExplorersByCourseIdRequest(Long courseId);

    void validateDeleteExplorerByIdRequest(Long explorerId);
}
