package org.example.person.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.person.exception.classes.course.CourseNotFoundException;
import org.example.person.exception.classes.explorer.ExplorerNotFoundException;
import org.example.person.exception.classes.person.PersonNotFoundException;
import org.example.person.exception.classes.progress.ExplorerAlreadyHasMarkException;
import org.example.person.repository.CourseMarkRepository;
import org.example.person.repository.CourseRepository;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExplorerValidatorServiceImpl implements ExplorerValidatorService {
    private final CourseRepository courseRepository;
    private final ExplorerRepository explorerRepository;
    private final PersonService personService;
    private final CourseMarkRepository courseMarkRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByPersonIdRequest(Long personId) {
        if (!personService.personExistsById(personId))
            throw new PersonNotFoundException(personId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByCourseIdRequest(Long courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDeleteExplorerByIdRequest(Long explorerId) {
        if (!explorerRepository.existsById(explorerId))
            throw new ExplorerNotFoundException(explorerId);
        if (courseMarkRepository.findById(explorerId).isPresent()) {
            throw new ExplorerAlreadyHasMarkException();
        }
    }
}
