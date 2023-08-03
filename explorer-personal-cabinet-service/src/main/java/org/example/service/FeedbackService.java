package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CourseRatingCreateRequest;
import org.example.dto.feedback.ExplorerFeedbackCreateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotOnCourseException;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.feedback.CourseRating;
import org.example.model.feedback.ExplorerFeedback;
import org.example.repository.*;
import org.example.validator.FeedbackValidator;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final FeedbackValidator feedbackValidator;

    private final ModelMapper mapper;

    public ExplorerFeedback sendFeedbackForKeeper(Integer courseId, ExplorerFeedbackCreateRequest feedback) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Keeper keeper = keeperRepository.findById(feedback.getKeeperId())
                .orElseThrow(() -> new KeeperNotFoundException(feedback.getKeeperId()));
        if (!courseId.equals(keeper.getCourseId()))
            throw new KeeperNotOnCourseException(keeper.getKeeperId(), courseId);
        Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        feedbackValidator.validateFeedbackForKeeperRequest(explorer.getExplorerId(), explorer.getCourseId());
        ExplorerFeedback savingFeedback = mapper.map(feedback, ExplorerFeedback.class);
        savingFeedback.setExplorerId(explorer.getExplorerId());
        return explorerFeedbackRepository.save(savingFeedback);
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public CourseRating rateCourse(Integer courseId, CourseRatingCreateRequest request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        feedbackValidator.validateCourseRatingRequest(explorer.getExplorerId(), courseId);
        return courseRatingRepository.save(
                new CourseRating(explorer.getExplorerId(), request.getRating())
        );
    }
}
