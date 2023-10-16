package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.CourseRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExplorerValidatorServiceImpl implements ExplorerValidatorService {
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final ExplorerRepository explorerRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByPersonIdRequest(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByCourseIdRequest(Integer courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDeleteExplorerByIdRequest(Integer explorerId) {
        if (!explorerRepository.existsById(explorerId))
            throw new ExplorerNotFoundException(explorerId);
    }
}
