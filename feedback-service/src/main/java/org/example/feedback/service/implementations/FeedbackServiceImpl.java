package org.example.feedback.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerFeedbackRepository;
import org.example.feedback.repository.KeeperFeedbackRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl {
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    @KafkaListener(topics = "deleteFeedbackTopic", containerFactory = "deleteFeedbackKafkaListenerContainerFactory")
    @Transactional
    public void deleteFeedbackByExplorerId(Long explorerId) {
        if (explorerFeedbackRepository.existsById(explorerId))
            explorerFeedbackRepository.deleteById(explorerId);
        if (keeperFeedbackRepository.existsById(explorerId))
            keeperFeedbackRepository.deleteById(explorerId);
        if (courseRatingRepository.existsById(explorerId))
            courseRatingRepository.deleteById(explorerId);
    }
}
