package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.feedbackEX.FeedbackAlreadyExists;
import org.example.exception.classes.progressEX.CourseNotCompletedException;
import org.example.repository.CourseMarkRepository;
import org.example.repository.CourseRatingRepository;
import org.example.repository.ExplorerFeedbackRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackValidator {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    public void validateFeedbackForKeeperRequest(Integer explorerId, Integer courseId) {
        if (!courseMarkRepository.existsById(explorerId))
            throw new CourseNotCompletedException(courseId);
        if (explorerFeedbackRepository.existsById(explorerId))
            throw new FeedbackAlreadyExists();
    }

    public void validateCourseRatingRequest(Integer explorerId, Integer courseId) {
        if (!courseMarkRepository.existsById(explorerId))
            throw new CourseNotCompletedException(courseId);
        if (courseRatingRepository.existsById(explorerId))
            throw new FeedbackAlreadyExists();
    }
}
