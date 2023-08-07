package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeAlreadyExistsException;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseThemeValidator {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    public void validateGetThemesByCourseIdRequest(Integer courseId) {
        if (courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    public void validatePutRequest(Integer themeId, CourseThemeUpdateRequest theme) {
        if (!courseRepository.existsById(theme.getCourseId()))
            throw new CourseNotFoundException(theme.getCourseId());
        boolean themeTitleExists = courseThemeRepository.findCourseThemesByCourseId(
                        theme.getCourseId()).stream()
                .anyMatch(t -> t.getTitle().equals(theme.getTitle()) && !t.getCourseThemeId().equals(themeId));
        if (themeTitleExists)
            throw new CourseThemeAlreadyExistsException(theme.getTitle());
    }
}
