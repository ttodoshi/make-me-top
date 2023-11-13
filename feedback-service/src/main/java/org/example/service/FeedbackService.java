package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.CourseRatingRepository;
import org.example.repository.ExplorerFeedbackRepository;
import org.example.repository.KeeperFeedbackRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    @KafkaListener(topics = "deleteFeedbackTopic", containerFactory = "deleteFeedbackKafkaListenerContainerFactory")
    @Transactional
    public void deleteFeedbackByExplorerId(Integer explorerId) {
        explorerFeedbackRepository.deleteById(explorerId);
        keeperFeedbackRepository.deleteById(explorerId);
        courseRatingRepository.deleteById(explorerId);
    }
}
