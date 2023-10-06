package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.feedback.CreateCourseRatingDto;
import org.example.dto.feedback.CreateExplorerFeedbackDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.CourseRating;
import org.example.model.ExplorerFeedback;
import org.example.repository.CourseRatingRepository;
import org.example.repository.ExplorerFeedbackRepository;
import org.example.repository.ExplorerRepository;
import org.example.service.validator.FeedbackValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplorerFeedbackService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final PersonService personService;
    private final FeedbackValidatorService feedbackValidatorService;
    private final KafkaTemplate<String, Integer> kafkaTemplate;

    private final ModelMapper mapper;

    public List<ExplorerFeedback> findExplorerFeedbacksByKeeperIdIn(List<Integer> keeperIds) {
        return explorerFeedbackRepository.findExplorerFeedbacksByKeeperIdIn(keeperIds);
    }

    public ExplorerFeedback sendFeedbackForKeeper(Integer courseId, CreateExplorerFeedbackDto feedback) {
        Integer personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateFeedbackForKeeperRequest(personId, feedback);
        ExplorerDto explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        ExplorerFeedback savingFeedback = mapper.map(feedback, ExplorerFeedback.class);
        savingFeedback.setExplorerId(explorer.getExplorerId());
        sendGalaxyCacheRefreshMessage(courseId);
        return explorerFeedbackRepository.save(savingFeedback);
    }

    // TODO
    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    public CourseRating rateCourse(Integer courseId, CreateCourseRatingDto request) {
        Integer personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateCourseRatingRequest(personId, courseId, request);
        ExplorerDto explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        return courseRatingRepository.save(
                new CourseRating(explorer.getExplorerId(), request.getRating())
        );
    }
}