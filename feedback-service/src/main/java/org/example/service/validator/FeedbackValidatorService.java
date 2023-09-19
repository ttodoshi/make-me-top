package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CreateCourseRatingDto;
import org.example.dto.feedback.CreateExplorerFeedbackDto;
import org.example.dto.feedback.CreateKeeperFeedbackDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackValidatorService {
    // TODO
//    private final CourseMarkRepository courseMarkRepository;
//    private final ExplorerRepository explorerRepository;
//    private final ExplorerGroupRepository explorerGroupRepository;
//    private final KeeperFeedbackRepository keeperFeedbackRepository;
//    private final ExplorerFeedbackRepository explorerFeedbackRepository;
//    private final CourseRatingRepository courseRatingRepository;
//    private final KeeperRepository keeperRepository;
//    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public void validateFeedbackForExplorerRequest(Integer keeperId, CreateKeeperFeedbackDto feedback) {
//        Explorer explorer = explorerRepository.findById(feedback.getExplorerId())
//                .orElseThrow(() -> new ExplorerNotFoundException(feedback.getExplorerId()));
//        boolean explorerNotOnCourse = explorerRepository.findExplorersForKeeper(keeperId)
//                .stream()
//                .noneMatch(e -> e.getExplorerId().equals(feedback.getExplorerId()));
//        if (explorerNotOnCourse)
//            throw new ExplorerNotStudyingWithKeeper(explorer.getExplorerId(), keeperId);
//        if (!courseMarkRepository.existsById(explorer.getExplorerId())) {
//            throw new CourseNotCompletedException(
//                    explorerGroupRepository.getReferenceById(explorer.getGroupId()).getCourseId()
//            );
//        }
//        if (keeperFeedbackRepository.existsById(explorer.getExplorerId()))
//            throw new FeedbackAlreadyExists();
//        if (feedback.getRating() < 1 || feedback.getRating() > 5)
//            throw new UnexpectedRatingValue();
    }

    public void validateFeedbackForKeeperRequest(Integer personId, CreateExplorerFeedbackDto feedback) {
//        Keeper keeper = keeperRepository.findById(feedback.getKeeperId())
//                .orElseThrow(() -> new KeeperNotFoundException(feedback.getKeeperId()));
//        if (!courseRepository.existsById(keeper.getCourseId()))
//            throw new CourseNotFoundException(keeper.getCourseId());
//        Explorer explorer = explorerRepository
//                .findExplorerByPersonIdAndCourseId(personId, keeper.getCourseId())
//                .orElseThrow(() -> new ExplorerNotFoundException(keeper.getCourseId()));
//        Keeper actualKeeper = keeperRepository.getKeeperForExplorer(explorer.getExplorerId());
//        if (!actualKeeper.getKeeperId().equals(feedback.getKeeperId()))
//            throw new DifferentKeeperException(actualKeeper.getKeeperId(), feedback.getKeeperId());
//        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
//            throw new CourseNotCompletedException(keeper.getCourseId());
//        if (explorerFeedbackRepository.existsById(explorer.getExplorerId()))
//            throw new FeedbackAlreadyExists();
//        if (feedback.getRating() < 1 || feedback.getRating() > 5)
//            throw new UnexpectedRatingValue();
    }

    public void validateCourseRatingRequest(Integer personId, Integer courseId, CreateCourseRatingDto request) {
//        if (!courseRepository.existsById(courseId))
//            throw new CourseNotFoundException(courseId);
//        Explorer explorer = explorerRepository
//                .findExplorerByPersonIdAndCourseId(personId, courseId)
//                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
//        if (!courseMarkRepository.existsById(explorer.getExplorerId()))
//            throw new CourseNotCompletedException(courseId);
//        if (courseRatingRepository.existsById(explorer.getExplorerId()))
//            throw new FeedbackAlreadyExists();
//        if (request.getRating() < 1 || request.getRating() > 5)
//            throw new UnexpectedRatingValue();
    }
}
