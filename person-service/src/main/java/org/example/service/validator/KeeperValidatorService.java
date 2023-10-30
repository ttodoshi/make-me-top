package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.CreateKeeperDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.CourseRepository;
import org.example.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KeeperValidatorService {
    private final CourseRepository courseRepository;
    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateKeepersByPersonIdRequest(Integer personId) {
        if (!personService.personExistsById(personId))
            throw new PersonNotFoundException(personId);
    }

    @Transactional(readOnly = true)
    public void validateKeepersByCourseIdRequest(Integer courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    @Transactional(readOnly = true)
    public void validateSetKeeperRequest(Integer courseId, CreateKeeperDto request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personService.personExistsById(request.getPersonId()))
            throw new PersonNotFoundException(request.getPersonId());
    }
}
