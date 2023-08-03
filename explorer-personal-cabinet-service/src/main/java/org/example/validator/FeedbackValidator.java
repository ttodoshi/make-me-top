package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CourseRatingCreateRequest;
import org.example.dto.feedback.ExplorerFeedbackCreateRequest;
import org.example.exception.classes.feedbackEX.FeedbackAlreadyExists;
import org.example.exception.classes.feedbackEX.UnexpectedRatingValue;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.progressEX.CourseNotCompletedException;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.repository.CourseMarkRepository;
import org.example.repository.CourseRatingRepository;
import org.example.repository.ExplorerFeedbackRepository;
import org.example.repository.KeeperRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackValidator {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final KeeperRepository keeperRepository;

    public void validateFeedbackForKeeperRequest(Explorer explorer, ExplorerFeedbackCreateRequest feedback) {
        Keeper actualKeeper = keeperRepository.getKeeperForPersonOnCourse(explorer.getPersonId(), explorer.getCourseId());
        if (!actualKeeper.getKeeperId().equals(feedback.getKeeperId()))
            throw new DifferentKeeperException(actualKeeper.getKeeperId(), feedback.getKeeperId());
        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseNotCompletedException(explorer.getCourseId());
        if (explorerFeedbackRepository.existsById(explorer.getExplorerId()))
            throw new FeedbackAlreadyExists();
        if (feedback.getRating() < 1 || feedback.getRating() > 5)
            throw new UnexpectedRatingValue();
    }

    public void validateCourseRatingRequest(Integer explorerId, Integer courseId, CourseRatingCreateRequest request) {
        if (!courseMarkRepository.existsById(explorerId))
            throw new CourseNotCompletedException(courseId);
        if (courseRatingRepository.existsById(explorerId))
            throw new FeedbackAlreadyExists();
        if (request.getRating() < 1 || request.getRating() > 5)
            throw new UnexpectedRatingValue();
    }
}
