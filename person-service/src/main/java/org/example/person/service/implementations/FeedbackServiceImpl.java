package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.feedback.ExplorerCommentDto;
import org.example.person.dto.feedback.ExplorerFeedbackDto;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.feedback.KeeperFeedbackDto;
import org.example.person.dto.feedback.offer.*;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.repository.*;
import org.example.person.service.ExplorerService;
import org.example.person.service.FeedbackService;
import org.example.person.service.KeeperService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRepository courseRepository;
    private final CourseRatingOfferRepository courseRatingOfferRepository;

    private final KeeperService keeperService;
    private final ExplorerService explorerService;

    @Override
    @Transactional(readOnly = true)
    public List<CourseRatingOfferProfileDto> getCourseRatingOffers(List<Long> explorerIds) {
        List<CourseRatingOfferDto> offers = courseRatingOfferRepository
                .findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(explorerIds);

        Map<Long, Explorer> explorers = explorerService.findExplorersByExplorerIdIn(
                offers.stream().map(CourseRatingOfferDto::getExplorerId).collect(Collectors.toList())
        );

        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                explorers.values().stream().map(e -> e.getGroup().getCourseId()).collect(Collectors.toList())
        );

        return offers.stream()
                .map(o -> {
                    CourseDto currentCourse = courses.get(
                            explorers.get(o.getExplorerId()).getGroup().getCourseId()
                    );
                    return new CourseRatingOfferProfileDto(
                            o.getExplorerId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerFeedbackOfferProfileDto> getExplorerFeedbackOffers(List<Long> explorerIds) {
        List<ExplorerFeedbackOfferDto> offers = explorerFeedbackOfferRepository
                .findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds);

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                offers.stream().map(ExplorerFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );

        List<Long> courseIds = keepers.values()
                .stream()
                .map(Keeper::getCourseId)
                .collect(Collectors.toList());
        Map<Long, CourseDto> courses = courseRepository
                .findCoursesByCourseIdIn(courseIds);

        return offers.stream()
                .map(o -> {
                    CourseDto currentCourse = courses.get(
                            keepers.get(o.getKeeperId()).getCourseId()
                    );
                    return new ExplorerFeedbackOfferProfileDto(
                            o.getExplorerId(),
                            keepers.get(o.getKeeperId()).getPersonId(),
                            keepers.get(o.getKeeperId()).getPerson().getFirstName(),
                            keepers.get(o.getKeeperId()).getPerson().getLastName(),
                            keepers.get(o.getKeeperId()).getPerson().getPatronymic(),
                            o.getKeeperId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeeperFeedbackOfferProfileDto> getKeeperFeedbackOffers(List<Long> explorerIds) {
        List<KeeperFeedbackOfferDto> offers = keeperFeedbackOfferRepository
                .findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds);

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                offers.stream().map(KeeperFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );

        List<Long> courseIds = keepers.values()
                .stream()
                .map(Keeper::getCourseId)
                .collect(Collectors.toList());
        Map<Long, CourseDto> courses = courseRepository
                .findCoursesByCourseIdIn(courseIds);

        return offers.stream()
                .map(o -> {
                    Explorer currentExplorer = explorerService
                            .findExplorerById(o.getExplorerId());
                    CourseDto currentCourse = courses.get(
                            keepers.get(o.getKeeperId()).getCourseId()
                    );
                    return new KeeperFeedbackOfferProfileDto(
                            o.getKeeperId(),
                            o.getExplorerId(),
                            currentExplorer.getPersonId(),
                            currentExplorer.getPerson().getFirstName(),
                            currentExplorer.getPerson().getLastName(),
                            currentExplorer.getPerson().getPatronymic(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<Explorer> personExplorers) {
        Map<Long, KeeperFeedbackOfferDto> feedbackOffers = keeperFeedbackRepository
                .findKeeperFeedbackOffersByExplorerIdIn(
                        personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                );

        List<KeeperFeedbackDto> feedbacks = keeperFeedbackRepository
                .findKeeperFeedbacksByIdIn(
                        feedbackOffers.values().stream().map(KeeperFeedbackOfferDto::getExplorerId).collect(Collectors.toList())
                );

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                feedbackOffers.values().stream().map(KeeperFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                keepers.values().stream().map(Keeper::getCourseId).collect(Collectors.toList())
        );

        return feedbacks.stream()
                .map(f -> {
                    KeeperFeedbackOfferDto currentOffer = feedbackOffers.get(f.getExplorerId());
                    Person person = keepers.get(currentOffer.getKeeperId()).getPerson();
                    CourseDto currentCourse = courses.get(
                            keepers.get(currentOffer.getKeeperId()).getCourseId()
                    );
                    return new KeeperCommentDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            currentOffer.getKeeperId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle(),
                            f.getRating(),
                            f.getComment()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroup> groups) {
        Map<Long, ExplorerFeedbackOfferDto> feedbackOffers = explorerFeedbackRepository
                .findExplorerFeedbackOffersByKeeperIdIn(
                        groups.stream().map(ExplorerGroup::getKeeperId).collect(Collectors.toList())
                );

        List<ExplorerFeedbackDto> feedbacks = explorerFeedbackRepository
                .findExplorerFeedbacksByIdIn(
                        feedbackOffers.values().stream().map(ExplorerFeedbackOfferDto::getExplorerId).collect(Collectors.toList())
                );

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                feedbackOffers.values().stream().map(ExplorerFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                groups.stream().map(ExplorerGroup::getCourseId).collect(Collectors.toList())
        );
        Map<Long, Explorer> explorers = groups.stream().flatMap(
                g -> g.getExplorers().stream()
        ).filter(e -> feedbackOffers.values()
                .stream()
                .map(ExplorerFeedbackOfferDto::getExplorerId)
                .collect(Collectors.toSet())
                .contains(e.getExplorerId())
        ).collect(Collectors.toMap(Explorer::getExplorerId, e -> e));

        return feedbacks.stream()
                .map(f -> {
                    ExplorerFeedbackOfferDto currentOffer = feedbackOffers.get(f.getExplorerId());
                    Person person = explorers.get(currentOffer.getExplorerId()).getPerson();
                    CourseDto currentCourse = courses.get(keepers.get(
                            currentOffer.getKeeperId()
                    ).getCourseId());
                    return new ExplorerCommentDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            currentOffer.getExplorerId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle(),
                            f.getRating(),
                            f.getComment()
                    );
                }).collect(Collectors.toList());
    }
}
