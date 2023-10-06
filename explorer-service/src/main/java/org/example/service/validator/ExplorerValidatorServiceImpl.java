package org.example.service.validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExplorerValidatorServiceImpl implements ExplorerValidatorService {
    @Override
    public void validateGetExplorersByPersonIdRequest(Integer personId) {
        // TODO person exists
    }

    @Override
    public void validateGetExplorersByCourseIdRequest(Integer courseId) {
        // TODO course exists
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDeleteExplorerByIdRequest(Integer explorerId) {
        // TODO explorer exists and other
    }
}
