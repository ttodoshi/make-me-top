package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.progressEX.CourseAlreadyCompletedException;
import org.example.model.Explorer;
import org.example.repository.ExplorerRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.courseprogress.CourseMarkRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseProgressValidatorService {
    private final ExplorerRepository explorerRepository;
    private final CourseRepository courseRepository;
    private final CourseMarkRepository courseMarkRepository;

    public void validateLeaveCourseRequest(Integer personId, Integer courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        if (courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseAlreadyCompletedException(courseId);
    }
}
