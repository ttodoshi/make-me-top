package org.example.validator;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.keeper.KeeperCreateRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.courseEX.CourseAlreadyExistsException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.courseEX.CourseNotFoundInGalaxyException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.course.Course;
import org.example.repository.CourseRepository;
import org.example.repository.PersonRepository;
import org.example.service.GalaxyRequestSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CourseValidator {
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;

    private final GalaxyRequestSender galaxyRequestSender;

    @Setter
    private String token;

    public void validatePutRequest(Integer galaxyId, Integer courseId, Course course) {
        galaxyRequestSender.setToken(token);
        StarSystemDTO[] systems = galaxyRequestSender.getSystemsByGalaxyId(galaxyId);
        boolean courseNotFound = Arrays.stream(systems)
                .noneMatch(s -> s.getSystemName().equals(course.getTitle()) && s.getSystemId().equals(courseId));
        if (courseNotFound)
            throw new CourseNotFoundInGalaxyException(courseId, galaxyId);
        boolean courseTitleExists = Arrays.stream(systems)
                .anyMatch(s -> s.getSystemName().equals(course.getTitle()) && !s.getSystemId().equals(courseId));
        if (courseTitleExists)
            throw new CourseAlreadyExistsException(course.getTitle());
    }

    public void validateSetKeeperRequest(Integer courseId, KeeperCreateRequest request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personRepository.existsById(request.getPersonId()))
            throw new PersonNotFoundException();
    }
}
