package org.example.course.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.system.StarSystemDto;
import org.example.course.exception.course.CourseAlreadyExistsException;
import org.example.course.exception.course.CourseNotFoundInGalaxyException;
import org.example.course.model.Course;
import org.example.course.service.StarSystemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseValidatorService {
    private final StarSystemService starSystemService;

    public void validatePutRequest(String authorizationHeader, Long galaxyId, Long courseId, Course course) {
        List<StarSystemDto> systems = starSystemService
                .findStarSystemsByGalaxyId(authorizationHeader, galaxyId);
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
