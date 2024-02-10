package org.example.feedback.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.feedback.dto.event.KeeperFeedbackOfferCreateEvent;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.dto.feedback.KeeperFeedbackDto;
import org.example.feedback.dto.offer.KeeperFeedbackOfferDto;
import org.example.feedback.exception.feedback.OfferNotFoundException;
import org.example.feedback.model.KeeperFeedback;
import org.example.feedback.model.KeeperFeedbackOffer;
import org.example.feedback.repository.KeeperFeedbackOfferRepository;
import org.example.feedback.repository.KeeperFeedbackRepository;
import org.example.feedback.service.KeeperFeedbackService;
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
public class KeeperFeedbackServiceImpl implements KeeperFeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;

    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(List<Long> feedbackIds) {
        return keeperFeedbackRepository
                .findAllById(feedbackIds)
                .stream()
                .map(f -> mapper.map(f, KeeperFeedbackDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdIn(List<Long> explorerIds) {
        return keeperFeedbackOfferRepository
                .findKeeperFeedbackOffersByExplorerIdIn(explorerIds)
                .stream()
                .collect(Collectors.toMap(
                        KeeperFeedbackOffer::getExplorerId,
                        o -> mapper.map(o, KeeperFeedbackOfferDto.class)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeeperFeedbackOfferDto> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(List<Long> explorerIds) {
        return keeperFeedbackOfferRepository
                .findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
                .stream()
                .map(o -> mapper.map(o, KeeperFeedbackOfferDto.class))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "createKeeperFeedbackOfferTopic", containerFactory = "createKeeperFeedbackOfferKafkaListenerContainerFactory")
    @Transactional
    public void createKeeperFeedbackOffer(KeeperFeedbackOfferCreateEvent offer) {
        keeperFeedbackOfferRepository.save(
                new KeeperFeedbackOffer(offer.getExplorerId(), offer.getKeeperId())
        );
    }

    @Override
    @Transactional
    public Long sendFeedbackForExplorer(String authorizationHeader, Long authenticatedPersonId, CreateKeeperFeedbackDto feedback) {
        feedbackValidatorService.validateFeedbackForExplorerRequest(
                authorizationHeader, authenticatedPersonId, feedback
        );

        keeperFeedbackOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                }).setOfferValid(false);

        return keeperFeedbackRepository.save(
                mapper.map(feedback, KeeperFeedback.class)
        ).getExplorerId();
    }

    @Override
    @Cacheable(cacheNames = "explorerRatingCache", key = "#explorerIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonExplorerIds(List<Long> explorerIds) {
        return Math.ceil(keeperFeedbackRepository.getPersonRatingAsExplorer(explorerIds).orElse(0.0) * 10) / 10;
    }

    @Override
    @Transactional
    public KeeperFeedbackOfferDto closeKeeperFeedbackOffer(String authorizationHeader, Long authenticatedPersonId, Long explorerId) {
        KeeperFeedbackOffer offer = keeperFeedbackOfferRepository.findById(explorerId)
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", explorerId);
                    return new OfferNotFoundException(explorerId);
                });

        feedbackValidatorService.validateCloseKeeperFeedbackOfferRequest(authorizationHeader, authenticatedPersonId, offer);

        offer.setOfferValid(false);
        return mapper.map(
                keeperFeedbackOfferRepository.save(
                        offer
                ), KeeperFeedbackOfferDto.class
        );
    }
}
