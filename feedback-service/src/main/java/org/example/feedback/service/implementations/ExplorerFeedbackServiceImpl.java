package org.example.feedback.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.feedback.dto.event.CourseRatingOfferCreateEvent;
import org.example.feedback.dto.event.ExplorerFeedbackOfferCreateEvent;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.ExplorerFeedbackDto;
import org.example.feedback.dto.offer.CourseRatingOfferDto;
import org.example.feedback.dto.offer.ExplorerFeedbackOfferDto;
import org.example.feedback.exception.feedback.OfferNotFoundException;
import org.example.feedback.model.CourseRating;
import org.example.feedback.model.CourseRatingOffer;
import org.example.feedback.model.ExplorerFeedback;
import org.example.feedback.model.ExplorerFeedbackOffer;
import org.example.feedback.repository.CourseRatingOfferRepository;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerFeedbackOfferRepository;
import org.example.feedback.repository.ExplorerFeedbackRepository;
import org.example.feedback.service.ExplorerFeedbackService;
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
@Slf4j
public class ExplorerFeedbackServiceImpl implements ExplorerFeedbackService {
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final CourseRatingOfferRepository courseRatingOfferRepository;

    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerFeedbackDto> findExplorerFeedbacksByIdIn(List<Long> feedbackIds) {
        return explorerFeedbackRepository
                .findAllById(feedbackIds)
                .stream()
                .map(o -> mapper.map(o, ExplorerFeedbackDto.class))
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerFeedbackOfferDto> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds) {
        return explorerFeedbackOfferRepository
                .findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
                .stream()
                .map(o -> mapper.map(o, ExplorerFeedbackOfferDto.class))
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
    @Transactional
    public Long sendFeedbackForKeeper(String authorizationHeader, Long authenticatedPersonId, CreateExplorerFeedbackDto feedback) {
        feedbackValidatorService.validateFeedbackForKeeperRequest(authorizationHeader, authenticatedPersonId, feedback);

        explorerFeedbackOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                })
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

    @Override
    @Transactional
    public Long rateCourse(String authorizationHeader, Long authenticatedPersonId, CreateCourseRatingDto feedback) {
        feedbackValidatorService.validateCourseRatingRequest(authorizationHeader, authenticatedPersonId, feedback);

        courseRatingOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                })
                .setOfferValid(false);

        return courseRatingRepository.save(
                mapper.map(feedback, CourseRating.class)
        ).getExplorerId();
    }

    @Override
    @Cacheable(cacheNames = "keeperRatingCache", key = "#keeperIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonKeeperIds(List<Long> keeperIds) {
        return Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(keeperIds).orElse(0.0) * 10) / 10;
    }

    @Override
    @Transactional
    public ExplorerFeedbackOfferDto closeExplorerFeedbackOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId) {
        ExplorerFeedbackOffer offer = explorerFeedbackOfferRepository.findById(explorerId)
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", explorerId);
                    return new OfferNotFoundException(explorerId);
                });

        feedbackValidatorService.validateCloseExplorerFeedbackOfferRequest(
                authorizationHeader, authenticatedPersonId, offer
        );

        offer.setOfferValid(false);
        return mapper.map(
                explorerFeedbackOfferRepository.save(
                        offer
                ), ExplorerFeedbackOfferDto.class
        );
    }

    @Override
    @Transactional
    public CourseRatingOfferDto closeCourseRatingOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId) {
        CourseRatingOffer offer = courseRatingOfferRepository.findById(explorerId)
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", explorerId);
                    return new OfferNotFoundException(explorerId);
                });

        feedbackValidatorService.validateCloseCourseRatingOfferRequest(authorizationHeader, authenticatedPersonId, offer);

        offer.setOfferValid(false);
        return mapper.map(
                courseRatingOfferRepository.save(
                        offer
                ), CourseRatingOfferDto.class
        );
    }
}
