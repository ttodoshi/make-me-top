package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.event.CourseRatingOfferCreateEvent;
import org.example.feedback.dto.event.ExplorerFeedbackOfferCreateEvent;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.ExplorerFeedbackDto;
import org.example.feedback.dto.offer.CourseRatingOfferDto;
import org.example.feedback.dto.offer.ExplorerFeedbackOfferDto;
import org.example.feedback.exception.classes.feedback.OfferNotFoundException;
import org.example.feedback.model.CourseRating;
import org.example.feedback.model.CourseRatingOffer;
import org.example.feedback.model.ExplorerFeedback;
import org.example.feedback.model.ExplorerFeedbackOffer;
import org.example.feedback.repository.CourseRatingOfferRepository;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerFeedbackOfferRepository;
import org.example.feedback.repository.ExplorerFeedbackRepository;
import org.example.feedback.service.validator.FeedbackValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerFeedbackService {
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final CourseRatingOfferRepository courseRatingOfferRepository;

    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<ExplorerFeedbackDto> findExplorerFeedbacksByIdIn(List<Long> feedbackIds) {
        return explorerFeedbackRepository
                .findAllById(feedbackIds)
                .stream()
                .map(o -> mapper.map(o, ExplorerFeedbackDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<Long, ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByKeeperIdIn(List<Long> keeperIds) {
        return explorerFeedbackOfferRepository
                .findExplorerFeedbackOffersByKeeperIdIn(keeperIds)
                .stream()
                .collect(Collectors.toMap(
                        ExplorerFeedbackOffer::getExplorerId,
                        o -> mapper.map(o, ExplorerFeedbackOfferDto.class)
                ));
    }

    @Transactional(readOnly = true)
    public List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds) {
        return explorerFeedbackOfferRepository
                .findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
                .stream()
                .map(o -> mapper.map(o, ExplorerFeedbackOfferDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseRatingOfferDto> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds) {
        return courseRatingOfferRepository
                .findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
                .stream()
                .map(o -> mapper.map(o, CourseRatingOfferDto.class))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "createExplorerFeedbackOfferTopic", containerFactory = "createExplorerFeedbackOfferKafkaListenerContainerFactory")
    @Transactional
    public void createExplorerFeedbackOffer(ExplorerFeedbackOfferCreateEvent offer) {
        explorerFeedbackOfferRepository.save(
                new ExplorerFeedbackOffer(offer.getKeeperId(), offer.getExplorerId())
        );
    }

    @Transactional
    public Long sendFeedbackForKeeper(CreateExplorerFeedbackDto feedback) {
        feedbackValidatorService.validateFeedbackForKeeperRequest(feedback);

        explorerFeedbackOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()))
                .setOfferValid(false);

        return explorerFeedbackRepository.save(
                mapper.map(feedback, ExplorerFeedback.class)
        ).getExplorerId();
    }

    @KafkaListener(topics = "createCourseRatingOfferTopic", containerFactory = "createCourseRatingOfferKafkaListenerContainerFactory")
    @Transactional
    public void createCourseRatingOffer(CourseRatingOfferCreateEvent offer) {
        courseRatingOfferRepository.save(
                new CourseRatingOffer(offer.getExplorerId())
        );
    }

    @Transactional
    public Long rateCourse(CreateCourseRatingDto feedback) {
        feedbackValidatorService.validateCourseRatingRequest(feedback);

        courseRatingOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()))
                .setOfferValid(false);

        return courseRatingRepository.save(
                mapper.map(feedback, CourseRating.class)
        ).getExplorerId();
    }

    @Cacheable(cacheNames = "keeperRatingCache", key = "#keeperIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonKeeperIds(List<Long> keeperIds) {
        return Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(keeperIds).orElse(0.0) * 10) / 10;
    }

    @Transactional
    public ExplorerFeedbackOfferDto closeExplorerFeedbackOffer(Long explorerId) {
        ExplorerFeedbackOffer offer = explorerFeedbackOfferRepository.findById(explorerId)
                .orElseThrow(() -> new OfferNotFoundException(explorerId));

        feedbackValidatorService.validateCloseExplorerFeedbackOfferRequest(offer);

        offer.setOfferValid(false);
        return mapper.map(
                explorerFeedbackOfferRepository.save(
                        offer
                ), ExplorerFeedbackOfferDto.class
        );
    }

    @Transactional
    public CourseRatingOfferDto closeCourseRatingOffer(Long explorerId) {
        CourseRatingOffer offer = courseRatingOfferRepository.findById(explorerId)
                .orElseThrow(() -> new OfferNotFoundException(explorerId));

        feedbackValidatorService.validateCloseCourseRatingOfferRequest(offer);

        offer.setOfferValid(false);
        return mapper.map(
                courseRatingOfferRepository.save(
                        offer
                ), CourseRatingOfferDto.class
        );
    }
}
