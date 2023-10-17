package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CreateKeeperFeedbackDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.model.KeeperFeedback;
import org.example.repository.CourseRepository;
import org.example.repository.KeeperFeedbackRepository;
import org.example.repository.KeeperRepository;
import org.example.service.validator.FeedbackValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeeperFeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<KeeperFeedback> findKeeperFeedbacksByExplorerIdIn(List<Integer> explorerIds) {
        return keeperFeedbackRepository.findKeeperFeedbacksByExplorerIdIn(explorerIds);
    }

    @Transactional
    public KeeperFeedback sendFeedbackForExplorer(Integer courseId, CreateKeeperFeedbackDto feedback) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Integer personId = personService.getAuthenticatedPersonId();
        KeeperDto keeper = keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
        feedbackValidatorService.validateFeedbackForExplorerRequest(keeper.getKeeperId(), feedback);
        KeeperFeedback savingFeedback = mapper.map(feedback, KeeperFeedback.class);
        savingFeedback.setKeeperId(keeper.getKeeperId());
        return keeperFeedbackRepository.save(savingFeedback);
    }
}
