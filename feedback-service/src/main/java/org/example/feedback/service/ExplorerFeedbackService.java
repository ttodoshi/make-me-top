package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.ExplorerFeedbackDto;
import org.example.feedback.exception.classes.explorer.ExplorerNotFoundException;
import org.example.feedback.model.CourseRating;
import org.example.feedback.model.ExplorerFeedback;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerFeedbackRepository;
import org.example.feedback.repository.ExplorerRepository;
import org.example.feedback.service.validator.FeedbackValidatorService;
import org.example.grpc.ExplorersService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerFeedbackService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final PersonService personService;
    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<ExplorerFeedbackDto> findExplorerFeedbacksByKeeperIdIn(List<Long> keeperIds) {
        return explorerFeedbackRepository
                .findExplorerFeedbacksByKeeperIdIn(keeperIds)
                .stream()
                .map(f -> mapper.map(f, ExplorerFeedbackDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long sendFeedbackForKeeper(Long courseId, CreateExplorerFeedbackDto feedback) {
        Long personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateFeedbackForKeeperRequest(personId, feedback);

        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));

        ExplorerFeedback savingFeedback = mapper.map(feedback, ExplorerFeedback.class);
        savingFeedback.setExplorerId(explorer.getExplorerId());

        return explorerFeedbackRepository
                .save(savingFeedback)
                .getExplorerId();
    }

    @Transactional
    public Long rateCourse(Long courseId, CreateCourseRatingDto request) {
        Long personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateCourseRatingRequest(personId, courseId, request);

        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));

        return courseRatingRepository.save(
                new CourseRating(explorer.getExplorerId(), request.getRating())
        ).getExplorerId();
    }

    @Cacheable(cacheNames = "keeperRatingCache", key = "#keeperIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonKeeperIds(List<Long> keeperIds) {
        return Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(keeperIds).orElse(0.0) * 10) / 10;
    }
}
