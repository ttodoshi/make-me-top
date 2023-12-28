package org.example.feedback.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.exception.classes.explorer.DifferentExplorerException;
import org.example.feedback.exception.classes.explorer.ExplorerNotFoundException;
import org.example.feedback.exception.classes.feedback.FeedbackAlreadyExistsException;
import org.example.feedback.exception.classes.feedback.OfferAlreadyNotValidException;
import org.example.feedback.exception.classes.feedback.OfferNotFoundException;
import org.example.feedback.exception.classes.keeper.DifferentKeeperException;
import org.example.feedback.exception.classes.keeper.KeeperNotFoundException;
import org.example.feedback.model.CourseRatingOffer;
import org.example.feedback.model.ExplorerFeedbackOffer;
import org.example.feedback.model.KeeperFeedbackOffer;
import org.example.feedback.repository.*;
import org.example.feedback.service.PersonService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackValidatorService {
    private final ExplorerRepository explorerRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;
    private final CourseRatingOfferRepository courseRatingOfferRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateFeedbackForExplorerRequest(CreateKeeperFeedbackDto feedback) {
        KeeperFeedbackOffer keeperFeedbackOffer = keeperFeedbackOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()));

        KeepersService.Keeper keeper = keeperRepository
                .findById(keeperFeedbackOffer.getKeeperId())
                .orElseThrow(() -> new KeeperNotFoundException(keeperFeedbackOffer.getKeeperId()));

        if (!personService.getAuthenticatedPersonId().equals(keeper.getPersonId()))
            throw new DifferentKeeperException();
        if (keeperFeedbackRepository.existsById(keeperFeedbackOffer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
    }

    @Transactional(readOnly = true)
    public void validateFeedbackForKeeperRequest(CreateExplorerFeedbackDto feedback) {
        ExplorerFeedbackOffer explorerFeedbackOffer = explorerFeedbackOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()));

        ExplorersService.Explorer explorer = explorerRepository
                .findById(explorerFeedbackOffer.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(explorerFeedbackOffer.getExplorerId()));

        if (!personService.getAuthenticatedPersonId().equals(explorer.getPersonId()))
            throw new DifferentExplorerException();
        if (explorerFeedbackRepository.existsById(explorerFeedbackOffer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
    }

    @Transactional(readOnly = true)
    public void validateCourseRatingRequest(CreateCourseRatingDto feedback) {
        CourseRatingOffer courseRatingOffer = courseRatingOfferRepository
                .findById(feedback.getExplorerId())
                .orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()));

        ExplorersService.Explorer explorer = explorerRepository
                .findById(courseRatingOffer.getExplorerId())
                .orElseThrow(ExplorerNotFoundException::new);

        if (!personService.getAuthenticatedPersonId().equals(explorer.getPersonId()))
            throw new DifferentExplorerException();
        if (courseRatingRepository.existsById(courseRatingOffer.getExplorerId()))
            throw new FeedbackAlreadyExistsException();
    }

    public void validateCloseExplorerFeedbackOfferRequest(ExplorerFeedbackOffer offer) {
        ExplorersService.Explorer explorer = explorerRepository
                .findById(offer.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(offer.getExplorerId()));
        if (!personService.getAuthenticatedPersonId().equals(explorer.getPersonId()))
            throw new DifferentExplorerException();
        if (!offer.getOfferValid())
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
    }

    public void validateCloseCourseRatingOfferRequest(CourseRatingOffer offer) {
        ExplorersService.Explorer explorer = explorerRepository
                .findById(offer.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(offer.getExplorerId()));
        if (!personService.getAuthenticatedPersonId().equals(explorer.getPersonId()))
            throw new DifferentExplorerException();
        if (!offer.getOfferValid())
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
    }

    public void validateCloseKeeperFeedbackOfferRequest(KeeperFeedbackOffer offer) {
        KeepersService.Keeper keeper = keeperRepository
                .findById(offer.getKeeperId())
                .orElseThrow(() -> new KeeperNotFoundException(offer.getKeeperId()));
        if (!personService.getAuthenticatedPersonId().equals(keeper.getPersonId()))
            throw new DifferentExplorerException();
        if (!offer.getOfferValid())
            throw new OfferAlreadyNotValidException(offer.getExplorerId());
    }
}
