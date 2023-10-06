package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.feedback.ExplorerCommentDto;
import org.example.dto.feedback.ExplorerFeedbackDto;
import org.example.dto.feedback.KeeperCommentDto;
import org.example.dto.feedback.KeeperFeedbackDto;
import org.example.dto.keeper.KeeperDto;
import org.example.model.Person;
import org.example.repository.*;
import org.example.service.FeedbackService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final PersonRepository personRepository;
    private final KeeperRepository keeperRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KeeperCommentDto> getFeedbackForPersonAsExplorer(List<ExplorerDto> personExplorers) {
        List<KeeperFeedbackDto> feedbacks = keeperFeedbackRepository
                .findKeeperFeedbacksByExplorerIdIn(
                        personExplorers.stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
                );
        Map<Integer, KeeperDto> keepers = keeperRepository.findKeepersByKeeperIdIn(
                feedbacks.stream().map(KeeperFeedbackDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                keepers.values().stream().map(KeeperDto::getCourseId).collect(Collectors.toList())
        );
        return feedbacks.stream()
                .map(f -> {
                    Person person = personRepository.getReferenceById(
                            keepers.get(f.getKeeperId()).getPersonId()
                    );
                    CourseDto currentCourse = courses.get(keepers.get(f.getKeeperId()).getCourseId());
                    return new KeeperCommentDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            f.getKeeperId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle(),
                            f.getRating(),
                            f.getComment()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerCommentDto> getFeedbackForPersonAsKeeper(List<ExplorerGroupDto> groups) {
        List<ExplorerFeedbackDto> feedbacks = explorerFeedbackRepository.findExplorerFeedbacksByKeeperIdIn(
                groups.stream().map(ExplorerGroupDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Integer, KeeperDto> keepers = keeperRepository.findKeepersByKeeperIdIn(
                feedbacks.stream().map(ExplorerFeedbackDto::getKeeperId).collect(Collectors.toList())
        );
        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                groups.stream().map(ExplorerGroupDto::getCourseId).collect(Collectors.toList())
        );
        Map<Integer, ExplorerDto> explorers = groups.stream().flatMap(
                g -> g.getExplorers().stream()
        ).filter(e -> feedbacks.stream()
                .map(ExplorerFeedbackDto::getExplorerId)
                .collect(Collectors.toList())
                .contains(e.getExplorerId())
        ).collect(Collectors.toMap(ExplorerDto::getExplorerId, e -> e));
        return feedbacks.stream()
                .map(f -> {
                    Person person = personRepository.getReferenceById(
                            explorers.get(f.getExplorerId()).getPersonId()
                    );
                    CourseDto currentCourse = courses.get(keepers.get(
                            f.getKeeperId()
                    ).getCourseId());
                    return new ExplorerCommentDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            f.getExplorerId(),
                            currentCourse.getCourseId(),
                            currentCourse.getTitle(),
                            f.getRating(),
                            f.getComment()
                    );
                }).collect(Collectors.toList());
    }
}