package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.KeeperFeedbackCreateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.feedback.KeeperFeedback;
import org.example.repository.course.CourseRepository;
import org.example.repository.feedback.KeeperFeedbackRepository;
import org.example.repository.KeeperRepository;
import org.example.service.validator.FeedbackValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    public KeeperFeedback sendFeedbackForExplorer(Integer courseId, KeeperFeedbackCreateRequest feedback) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Integer personId = getAuthenticatedPersonId();
        Keeper keeper = keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
        feedbackValidatorService.validateFeedbackForExplorerRequest(keeper.getKeeperId(), feedback);
        KeeperFeedback savingFeedback = mapper.map(feedback, KeeperFeedback.class);
        savingFeedback.setKeeperId(keeper.getKeeperId());
        return keeperFeedbackRepository.save(savingFeedback);
    }

    private Integer getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }
}
