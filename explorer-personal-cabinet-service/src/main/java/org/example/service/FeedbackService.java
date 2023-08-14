package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CourseRatingCreateRequest;
import org.example.dto.feedback.ExplorerFeedbackCreateRequest;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.feedback.CourseRating;
import org.example.model.feedback.ExplorerFeedback;
import org.example.repository.ExplorerRepository;
import org.example.repository.feedback.CourseRatingRepository;
import org.example.repository.feedback.ExplorerFeedbackRepository;
import org.example.service.validator.FeedbackValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final FeedbackValidatorService feedbackValidatorService;
    private final KafkaTemplate<String, Integer> kafkaTemplate;

    private final ModelMapper mapper;

    public ExplorerFeedback sendFeedbackForKeeper(Integer courseId, ExplorerFeedbackCreateRequest feedback) {
        feedbackValidatorService.keeperExists(courseId, feedback.getKeeperId());
        Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        feedbackValidatorService.validateFeedbackForKeeperRequest(explorer, feedback);
        ExplorerFeedback savingFeedback = mapper.map(feedback, ExplorerFeedback.class);
        savingFeedback.setExplorerId(explorer.getExplorerId());
        sendGalaxyCacheRefreshMessage(courseId);
        return explorerFeedbackRepository.save(savingFeedback);
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    public CourseRating rateCourse(Integer courseId, CourseRatingCreateRequest request) {
        feedbackValidatorService.courseExists(courseId);
        Integer personId = getAuthenticatedPersonId();
        Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        feedbackValidatorService.validateCourseRatingRequest(explorer.getExplorerId(), courseId, request);
        return courseRatingRepository.save(
                new CourseRating(explorer.getExplorerId(), request.getRating())
        );
    }
}
