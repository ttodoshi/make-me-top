package org.example.person.service.implementations.feedback;

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
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.feedback.*;
import org.example.person.service.implementations.ExplorerService;
import org.example.person.service.implementations.KeeperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final KeeperFeedbackService keeperFeedbackService;
    private final KeeperFeedbackOfferService keeperFeedbackOfferService;
    private final ExplorerFeedbackOfferService explorerFeedbackOfferService;
    private final ExplorerFeedbackService explorerFeedbackService;
    private final CourseService courseService;
    private final CourseRatingOfferService courseRatingOfferService;

    private final KeeperService keeperService;
    private final ExplorerService explorerService;

    @Override
    @Transactional(readOnly = true)
    public List<CourseRatingOfferProfileDto> getCourseRatingOffers(String authorizationHeader, List<Long> explorerIds) {
        List<CourseRatingOfferDto> offers = courseRatingOfferService
                .findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(authorizationHeader, explorerIds);

        Map<Long, Explorer> explorers = explorerService.findExplorersByExplorerIdIn(
                offers.stream().map(CourseRatingOfferDto::getExplorerId).collect(Collectors.toList())
        );

        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
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
    public List<ExplorerFeedbackOfferProfileDto> getExplorerFeedbackOffers(String authorizationHeader, List<Long> explorerIds) {
        List<ExplorerFeedbackOfferDto> offers = explorerFeedbackOfferService
                .findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(authorizationHeader, explorerIds);

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                offers.stream().map(ExplorerFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = findCoursesForKeepers(authorizationHeader, keepers);

        return offers.stream()
                .map(o -> {
                    Keeper currentKeeper = keepers.get(o.getKeeperId());
                    CourseDto currentCourse = courses.get(
                            currentKeeper.getCourseId()
                    );
                    return new ExplorerFeedbackOfferProfileDto(
                            o.getExplorerId(),
                            currentKeeper.getPersonId(),
                            currentKeeper.getPerson().getFirstName(),
                            currentKeeper.getPerson().getLastName(),
                            currentKeeper.getPerson().getPatronymic(),
                            o.getKeeperId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeeperFeedbackOfferProfileDto> getKeeperFeedbackOffers(String authorizationHeader, List<Long> explorerIds) {
        List<KeeperFeedbackOfferDto> offers = keeperFeedbackOfferService
                .findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(authorizationHeader, explorerIds);

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                offers.stream().map(KeeperFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = findCoursesForKeepers(authorizationHeader, keepers);

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

    private Map<Long, CourseDto> findCoursesForKeepers(String authorizationHeader, Map<Long, Keeper> keepers) {
        List<Long> courseIds = keepers.values()
                .stream()
                .map(Keeper::getCourseId)
                .collect(Collectors.toList());
        return courseService.findCoursesByCourseIdIn(authorizationHeader, courseIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeeperCommentDto> getFeedbackForPersonAsExplorer(String authorizationHeader, List<Explorer> personExplorers) {
        Map<Long, KeeperFeedbackOfferDto> feedbackOffers = keeperFeedbackOfferService
                .findKeeperFeedbackOffersByExplorerIdIn(
                        authorizationHeader,
                        personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                );

        List<KeeperFeedbackDto> feedbacks = keeperFeedbackService.findKeeperFeedbacksByIdIn(
                authorizationHeader,
                feedbackOffers.values().stream().map(KeeperFeedbackOfferDto::getExplorerId).collect(Collectors.toList())
        );

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                feedbackOffers.values().stream().map(KeeperFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
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
                            person.getPersonId(), person.getFirstName(),
                            person.getLastName(), person.getPatronymic(),
                            currentOffer.getKeeperId(),
                            currentCourse.getCourseId(), currentCourse.getTitle(),
                            f.getRating(), f.getComment()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(String authorizationHeader, List<ExplorerGroup> groups) {
        Map<Long, ExplorerFeedbackOfferDto> feedbackOffers = explorerFeedbackOfferService
                .findExplorerFeedbackOffersByKeeperIdIn(
                        authorizationHeader,
                        groups.stream().map(ExplorerGroup::getKeeperId).collect(Collectors.toList())
                );

        List<ExplorerFeedbackDto> feedbacks = explorerFeedbackService
                .findExplorerFeedbacksByIdIn(
                        authorizationHeader,
                        feedbackOffers.values().stream().map(ExplorerFeedbackOfferDto::getExplorerId).collect(Collectors.toList())
                );

        Map<Long, Keeper> keepers = keeperService.findKeepersByKeeperIdIn(
                feedbackOffers.values().stream().map(ExplorerFeedbackOfferDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
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
                            person.getPersonId(), person.getFirstName(),
                            person.getLastName(), person.getPatronymic(),
                            currentOffer.getExplorerId(),
                            currentCourse.getCourseId(), currentCourse.getTitle(),
                            f.getRating(), f.getComment()
                    );
                }).collect(Collectors.toList());
    }
}
