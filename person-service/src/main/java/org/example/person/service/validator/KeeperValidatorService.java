package org.example.person.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.exception.classes.course.CourseNotFoundException;
import org.example.person.exception.classes.person.PersonNotFoundException;
import org.example.person.repository.CourseRepository;
import org.example.person.service.PersonService;
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
