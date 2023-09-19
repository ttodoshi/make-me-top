package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDto;
import org.example.exception.classes.courseEX.CourseAlreadyExistsException;
import org.example.exception.classes.courseEX.CourseNotFoundInGalaxyException;
import org.example.model.Course;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseValidatorService {
    private final StarSystemRepository starSystemRepository;

    public void validatePutRequest(Integer galaxyId, Integer courseId, Course course) {
        List<StarSystemDto> systems = starSystemRepository.getSystemsByGalaxyId(galaxyId);
        boolean courseNotFound = systems.stream()
                .noneMatch(s -> s.getSystemName().equals(course.getTitle()) &&
                        s.getSystemId().equals(courseId));
        if (courseNotFound)
            throw new CourseNotFoundInGalaxyException(courseId, galaxyId);
        boolean courseTitleExists = systems.stream()
                .anyMatch(s -> s.getSystemName().equals(course.getTitle()) &&
                        !s.getSystemId().equals(courseId));
        if (courseTitleExists)
            throw new CourseAlreadyExistsException(course.getTitle());
    }
}
