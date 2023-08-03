package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.KeeperFeedbackCreateRequest;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotStudyingWithKeeper;
import org.example.exception.classes.feedbackEX.FeedbackAlreadyExists;
import org.example.exception.classes.feedbackEX.UnexpectedRatingValue;
import org.example.exception.classes.progressEX.CourseNotCompletedException;
import org.example.model.Explorer;
import org.example.repository.CourseMarkRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperFeedbackRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackValidator {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;

    public void validateFeedbackForExplorerRequest(Integer keeperId, KeeperFeedbackCreateRequest feedback) {
        Explorer explorer = explorerRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(feedback.getExplorerId()));
        boolean explorerNotOnCourse = explorerRepository.findExplorersForKeeper(keeperId)
                .stream()
                .noneMatch(e -> e.getExplorerId().equals(feedback.getExplorerId()));
        if (explorerNotOnCourse)
            throw new ExplorerNotStudyingWithKeeper(explorer.getExplorerId(), keeperId);
        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseNotCompletedException(explorer.getCourseId());
        if (keeperFeedbackRepository.existsById(explorer.getExplorerId()))
            throw new FeedbackAlreadyExists();
        if (feedback.getRating() < 1 || feedback.getRating() > 5)
            throw new UnexpectedRatingValue();
    }
}
