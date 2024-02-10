package org.example.feedback.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.exception.explorer.DifferentExplorerException;
import org.example.feedback.exception.feedback.FeedbackAlreadyExistsException;
import org.example.feedback.exception.feedback.OfferAlreadyNotValidException;
import org.example.feedback.exception.feedback.OfferNotFoundException;
import org.example.feedback.exception.keeper.DifferentKeeperException;
import org.example.feedback.model.CourseRatingOffer;
import org.example.feedback.model.ExplorerFeedbackOffer;
import org.example.feedback.model.KeeperFeedbackOffer;
import org.example.feedback.repository.*;
import org.example.feedback.service.ExplorerService;
import org.example.feedback.service.KeeperService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackValidatorService {
    private final ExplorerService explorerService;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;
    private final CourseRatingOfferRepository courseRatingOfferRepository;
    private final KeeperService keeperService;

    @Transactional(readOnly = true)
    public void validateFeedbackForExplorerRequest(String authorizationHeader, Long authenticatedPersonId, CreateKeeperFeedbackDto feedback) {
        KeeperFeedbackOffer keeperFeedbackOffer = keeperFeedbackOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                });

        KeepersService.Keeper keeper = keeperService
                .findById(authorizationHeader, keeperFeedbackOffer.getKeeperId());

        if (!authenticatedPersonId.equals(keeper.getPersonId())) {
            log.warn("authenticated person is not keeper for keeper feedback with id {}", feedback.getExplorerId());
            throw new DifferentKeeperException();
        }
        if (keeperFeedbackRepository.existsById(keeperFeedbackOffer.getExplorerId())) {
            log.warn("keeper feedback with id {} already exists", feedback.getExplorerId());
            throw new FeedbackAlreadyExistsException();
        }
    }

    @Transactional(readOnly = true)
    public void validateFeedbackForKeeperRequest(String authorizationHeader, Long authenticatedPersonId, CreateExplorerFeedbackDto feedback) {
        ExplorerFeedbackOffer explorerFeedbackOffer = explorerFeedbackOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                });

        ExplorersService.Explorer explorer = explorerService
                .findById(authorizationHeader, explorerFeedbackOffer.getExplorerId());

        if (!authenticatedPersonId.equals(explorer.getPersonId())) {
            log.warn("authenticated person is not explorer for explorer feedback with id {}", feedback.getExplorerId());
            throw new DifferentExplorerException();
        }
        if (explorerFeedbackRepository.existsById(explorerFeedbackOffer.getExplorerId())) {
            log.warn("explorer feedback with id {} already exists", feedback.getExplorerId());
            throw new FeedbackAlreadyExistsException();
        }
    }

    @Transactional(readOnly = true)
    public void validateCourseRatingRequest(String authorizationHeader, Long authenticatedPersonId, CreateCourseRatingDto feedback) {
        CourseRatingOffer courseRatingOffer = courseRatingOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> {
                    log.warn("feedback offer by id {} not found", feedback.getExplorerId());
                    return new OfferNotFoundException(feedback.getExplorerId());
                });

        ExplorersService.Explorer explorer = explorerService
                .findById(authorizationHeader, courseRatingOffer.getExplorerId());

        if (!authenticatedPersonId.equals(explorer.getPersonId())) {
            log.warn("authenticated person is not explorer for course feedback with id {}", feedback.getExplorerId());
            throw new DifferentExplorerException();
        }
        if (courseRatingRepository.existsById(courseRatingOffer.getExplorerId())) {
            log.warn("course feedback with id {} already exists", feedback.getExplorerId());
            throw new FeedbackAlreadyExistsException();
        }
    }

    public void validateCloseExplorerFeedbackOfferRequest(String authorizationHeader, Long authenticatedPersonId, ExplorerFeedbackOffer offer) {
        ExplorersService.Explorer explorer = explorerService
                .findById(authorizationHeader, offer.getExplorerId());
        if (!authenticatedPersonId.equals(explorer.getPersonId())) {
            log.warn("authenticated person is not explorer for explorer feedback with id {}", offer.getExplorerId());
            throw new DifferentExplorerException();
        }
        if (!offer.getOfferValid()) {
            log.warn("explorer feedback offer with id {} already closed", offer.getExplorerId());
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
        }
    }

    public void validateCloseCourseRatingOfferRequest(String authorizationHeader, Long authenticatedPersonId, CourseRatingOffer offer) {
        ExplorersService.Explorer explorer = explorerService
                .findById(authorizationHeader, offer.getExplorerId());
        if (!authenticatedPersonId.equals(explorer.getPersonId())) {
            log.warn("authenticated person is not explorer for course feedback with id {}", offer.getExplorerId());
            throw new DifferentExplorerException();
        }
        if (!offer.getOfferValid()) {
            log.warn("course feedback offer with id {} already closed", offer.getExplorerId());
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
        }
    }

    public void validateCloseKeeperFeedbackOfferRequest(String authorizationHeader, Long authenticatedPersonId, KeeperFeedbackOffer offer) {
        KeepersService.Keeper keeper = keeperService
                .findById(authorizationHeader, offer.getKeeperId());
        if (!authenticatedPersonId.equals(keeper.getPersonId())) {
            log.warn("authenticated person is not keeper for keeper feedback with id {}", offer.getExplorerId());
            throw new DifferentKeeperException();
        }
        if (!offer.getOfferValid()) {
            log.warn("keeper feedback offer with id {} already closed", offer.getExplorerId());
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
        }
    }
}
