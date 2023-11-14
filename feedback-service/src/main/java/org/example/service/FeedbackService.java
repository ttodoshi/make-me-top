package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.CourseRatingRepository;
import org.example.repository.ExplorerFeedbackRepository;
import org.example.repository.KeeperFeedbackRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    @KafkaListener(topics = "deleteFeedbackTopic", containerFactory = "deleteFeedbackKafkaListenerContainerFactory")
    @Transactional
    public void deleteFeedbackByExplorerId(Integer explorerId) {
        if (explorerFeedbackRepository.existsById(explorerId))
            explorerFeedbackRepository.deleteById(explorerId);
        if (keeperFeedbackRepository.existsById(explorerId))
            keeperFeedbackRepository.deleteById(explorerId);
        if (courseRatingRepository.existsById(explorerId))
            courseRatingRepository.deleteById(explorerId);
    }
}
