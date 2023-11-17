package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CreateCourseRatingDto;
import org.example.dto.feedback.CreateExplorerFeedbackDto;
import org.example.dto.feedback.CreateKeeperFeedbackDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotStudyingWithKeeperException;
import org.example.exception.classes.feedbackEX.FeedbackAlreadyExistsException;
import org.example.exception.classes.feedbackEX.UnexpectedRatingValueException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.progressEX.CourseNotCompletedException;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackValidatorService {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public void validateFeedbackForExplorerRequest(Integer keeperId, CreateKeeperFeedbackDto feedback) {
        ExplorersService.Explorer explorer = explorerRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(feedback.getExplorerId()));
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.getReferenceById(explorer.getGroupId());
        if (!keeperId.equals(explorerGroup.getKeeperId()))
            throw new ExplorerNotStudyingWithKeeperException(explorer.getExplorerId(), keeperId);
        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseNotCompletedException(explorerGroup.getCourseId());
        if (keeperFeedbackRepository.existsById(explorer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
        if (feedback.getRating() < 1 || feedback.getRating() > 5)
            throw new UnexpectedRatingValueException();
    }

    @Transactional(readOnly = true)
    public void validateFeedbackForKeeperRequest(Integer personId, CreateExplorerFeedbackDto feedback) {
        KeepersService.Keeper keeper = keeperRepository.findById(feedback.getKeeperId())
                .orElseThrow(() -> new KeeperNotFoundException(feedback.getKeeperId()));
        if (!courseRepository.existsById(keeper.getCourseId()))
            throw new CourseNotFoundException(keeper.getCourseId());
        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, keeper.getCourseId())
                .orElseThrow(() -> new ExplorerNotFoundException(keeper.getCourseId()));
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.getReferenceById(explorer.getGroupId());
        if (!feedback.getKeeperId().equals(explorerGroup.getKeeperId()))
            throw new DifferentKeeperException();
        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseNotCompletedException(keeper.getCourseId());
        if (explorerFeedbackRepository.existsById(explorer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
        if (feedback.getRating() < 1 || feedback.getRating() > 5)
            throw new UnexpectedRatingValueException();
    }

    @Transactional(readOnly = true)
    public void validateCourseRatingRequest(Integer personId, Integer courseId, CreateCourseRatingDto request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
            throw new CourseNotCompletedException(courseId);
        if (courseRatingRepository.existsById(explorer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
        if (request.getRating() < 1 || request.getRating() > 5)
            throw new UnexpectedRatingValueException();
    }
}
