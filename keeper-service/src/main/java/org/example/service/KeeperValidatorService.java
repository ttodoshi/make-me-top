package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.CreateKeeperDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.CourseRepository;
import org.example.repository.PersonRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeeperValidatorService {
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;

    public void validateKeepersByPersonIdRequest(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
    }

    public void validateKeepersByCourseIdRequest(Integer courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    public void validateSetKeeperRequest(Integer courseId, CreateKeeperDto request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personRepository.existsById(request.getPersonId()))
            throw new PersonNotFoundException(request.getPersonId());
    }
}
