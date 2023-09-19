package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.course.CourseThemeDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.homework.GetHomeworkRequestDto;
import org.example.dto.homework.HomeworkDto;
import org.example.dto.homework.HomeworkRequestDto;
import org.example.model.Person;
import org.example.repository.*;
import org.example.service.HomeworkService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Integer, ExplorerGroupDto> explorerGroups) {
        Map<Integer, ExplorerDto> explorers = explorerGroups.values()
                .stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toMap(ExplorerDto::getExplorerId, e -> e));
        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                explorerGroups.values().stream().map(ExplorerGroupDto::getCourseId).collect(Collectors.toList())
        );
        List<HomeworkRequestDto> homeworkRequests = homeworkRequestRepository.findOpenedHomeworkRequestsByExplorerIdIn(
                explorers.values().stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
        );
        Map<Integer, HomeworkDto> homeworks = homeworkRepository.findHomeworksByHomeworkIdIn(
                homeworkRequests.stream().map(HomeworkRequestDto::getHomeworkId).collect(Collectors.toList())
        );
        Map<Integer, CourseThemeDto> themes = courseThemeRepository.findCourseThemesByCourseThemeIdIn(
                homeworks.values().stream().map(HomeworkDto::getCourseThemeId).collect(Collectors.toList())
        );
        return homeworkRequests.stream()
                .map(hr -> {
                    ExplorerDto currentRequestExplorer = explorers.get(hr.getExplorerId());
                    Person person = personRepository.getReferenceById(
                            currentRequestExplorer.getPersonId()
                    );
                    CourseDto currentRequestCourse = courses.get(
                            explorerGroups.get(
                                    currentRequestExplorer.getGroupId()
                            ).getCourseId()
                    );
                    CourseThemeDto currentRequestTheme = themes.get(
                            homeworks.get(hr.getHomeworkId()).getCourseThemeId()
                    );
                    return new GetHomeworkRequestDto(
                            hr.getRequestId(),
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            currentRequestCourse.getCourseId(),
                            currentRequestCourse.getTitle(),
                            hr.getExplorerId(),
                            currentRequestTheme.getCourseThemeId(),
                            currentRequestTheme.getTitle(),
                            hr.getHomeworkId()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<GetHomeworkRequestDto> getHomeworkRequestForKeeperFromPerson(Integer keeperPersonId, List<ExplorerDto> personExplorers) {
        List<HomeworkRequestDto> openedHomeworkRequests = homeworkRequestRepository.findOpenedHomeworkRequestsByExplorerIdIn(
                personExplorers.stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
        );
        return openedHomeworkRequests.stream()
                .findAny()
                .map(hr -> {
                    ExplorerDto explorer = explorerRepository.getReferenceById(hr.getExplorerId());
                    Person person = personRepository.getReferenceById(explorer.getPersonId());
                    Integer courseId = explorerGroupRepository.getReferenceById(
                            explorer.getGroupId()
                    ).getCourseId();
                    CourseDto requestCourse = courseRepository.getReferenceById(courseId);
                    CourseThemeDto requestTheme = courseThemeRepository.getReferenceById(
                            homeworkRepository.getReferenceById(
                                    hr.getHomeworkId()
                            ).getCourseThemeId()
                    );
                    return new GetHomeworkRequestDto(
                            hr.getRequestId(),
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            requestCourse.getCourseId(),
                            requestCourse.getTitle(),
                            hr.getExplorerId(),
                            requestTheme.getCourseThemeId(),
                            requestTheme.getTitle(),
                            hr.getHomeworkId()
                    );
                });
    }
}
