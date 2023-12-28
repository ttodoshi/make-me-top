package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.event.KeeperFeedbackOfferCreateEvent;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.dto.feedback.KeeperFeedbackDto;
import org.example.feedback.dto.offer.KeeperFeedbackOfferDto;
import org.example.feedback.exception.classes.feedback.OfferNotFoundException;
import org.example.feedback.model.KeeperFeedback;
import org.example.feedback.model.KeeperFeedbackOffer;
import org.example.feedback.repository.KeeperFeedbackOfferRepository;
import org.example.feedback.repository.KeeperFeedbackRepository;
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
public class KeeperFeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;

    private final FeedbackValidatorService feedbackValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<KeeperFeedbackDto> findKeeperFeedbacksByIdIn(List<Long> feedbackIds) {
        return keeperFeedbackRepository
                .findAllById(feedbackIds)
                .stream()
                .map(f -> mapper.map(f, KeeperFeedbackDto.class))
                .collect(Collectors.toList());
    }

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

    @Transactional
    public Long sendFeedbackForExplorer(CreateKeeperFeedbackDto feedback) {
        feedbackValidatorService.validateFeedbackForExplorerRequest(feedback);

        keeperFeedbackOfferRepository.findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()))
                .setOfferValid(false);

        return keeperFeedbackRepository.save(
                mapper.map(feedback, KeeperFeedback.class)
        ).getExplorerId();
    }

    @Cacheable(cacheNames = "explorerRatingCache", key = "#explorerIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonExplorerIds(List<Long> explorerIds) {
        return Math.ceil(keeperFeedbackRepository.getPersonRatingAsExplorer(explorerIds).orElse(0.0) * 10) / 10;
    }

    @Transactional
    public KeeperFeedbackOfferDto closeKeeperFeedbackOffer(Long explorerId) {
        KeeperFeedbackOffer offer = keeperFeedbackOfferRepository.findById(explorerId)
                .orElseThrow(() -> new OfferNotFoundException(explorerId));

        feedbackValidatorService.validateCloseKeeperFeedbackOfferRequest(offer);

        offer.setOfferValid(false);
        return mapper.map(
                keeperFeedbackOfferRepository.save(
                        offer
                ), KeeperFeedbackOfferDto.class
        );
    }
}
