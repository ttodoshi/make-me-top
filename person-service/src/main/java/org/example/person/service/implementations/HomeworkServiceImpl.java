package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.homework.GetHomeworkRequestDto;
import org.example.person.dto.homework.HomeworkDto;
import org.example.person.dto.homework.HomeworkRequestDto;
import org.example.person.dto.planet.PlanetDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Person;
import org.example.person.repository.*;
import org.example.person.service.HomeworkService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final CourseRepository courseRepository;
    private final PlanetRepository planetRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Long, ExplorerGroup> explorerGroups) {
        Map<Long, Explorer> explorers = explorerGroups.values()
                .stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toMap(Explorer::getExplorerId, e -> e));
        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                explorerGroups.values().stream().map(ExplorerGroup::getCourseId).collect(Collectors.toList())
        );

        List<HomeworkRequestDto> homeworkRequests = homeworkRequestRepository.findOpenedHomeworkRequestsByExplorerIdIn(
                explorers.values().stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        Map<Long, HomeworkDto> homeworks = homeworkRepository.findHomeworksByHomeworkIdIn(
                homeworkRequests.stream().map(HomeworkRequestDto::getHomeworkId).collect(Collectors.toList())
        );

        Map<Long, PlanetDto> planets = planetRepository.findPlanetsByPlanetIdIn(
                homeworks.values().stream().map(HomeworkDto::getCourseThemeId).collect(Collectors.toList())
        );
        return homeworkRequests.stream()
                .map(hr -> {
                    Explorer currentRequestExplorer = explorers.get(hr.getExplorerId());
                    Person person = currentRequestExplorer.getPerson();
                    CourseDto currentRequestCourse = courses.get(
                            explorerGroups.get(
                                    currentRequestExplorer.getGroupId()
                            ).getCourseId()
                    );
                    PlanetDto currentRequestPlanet = planets.get(
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
                            currentRequestPlanet.getPlanetId(),
                            currentRequestPlanet.getPlanetName(),
                            hr.getHomeworkId(),
                            hr.getStatus()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequestsFromPerson(List<Explorer> personExplorers) {
        List<HomeworkRequestDto> openedHomeworkRequests = homeworkRequestRepository.findOpenedHomeworkRequestsByExplorerIdIn(
                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        Map<Long, HomeworkDto> homeworks = homeworkRepository.findHomeworksByHomeworkIdIn(
                openedHomeworkRequests.stream().map(HomeworkRequestDto::getHomeworkId).collect(Collectors.toList())
        );

        Map<Long, PlanetDto> planets = planetRepository.findPlanetsByPlanetIdIn(
                homeworks.values()
                        .stream()
                        .map(HomeworkDto::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                planets.values()
                        .stream()
                        .map(PlanetDto::getSystemId)
                        .collect(Collectors.toList())
        );

        return openedHomeworkRequests
                .stream()
                .map(hr -> {
                    Explorer explorer = explorerRepository.getReferenceById(hr.getExplorerId());
                    Person person = explorer.getPerson();
                    Long courseId = explorerGroupRepository.getReferenceById(
                            explorer.getGroupId()
                    ).getCourseId();
                    CourseDto requestCourse = courses.get(courseId);
                    PlanetDto requestTheme = planets.get(
                            homeworks.get(hr.getHomeworkId()).getCourseThemeId()
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
                            requestTheme.getPlanetId(),
                            requestTheme.getPlanetName(),
                            hr.getHomeworkId(),
                            hr.getStatus()
                    );
                }).collect(Collectors.toList());
    }
}
