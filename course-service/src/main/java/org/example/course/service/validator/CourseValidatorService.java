package org.example.course.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.starsystem.StarSystemDto;
import org.example.course.exception.classes.course.CourseAlreadyExistsException;
import org.example.course.exception.classes.course.CourseNotFoundInGalaxyException;
import org.example.course.model.Course;
import org.example.course.repository.StarSystemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseValidatorService {
    private final StarSystemRepository starSystemRepository;

    public void validatePutRequest(Long galaxyId, Long courseId, Course course) {
        List<StarSystemDto> systems = starSystemRepository.findStarSystemsByGalaxyId(galaxyId);
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
