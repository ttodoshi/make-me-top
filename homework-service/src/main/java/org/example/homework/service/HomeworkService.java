package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.dto.group.GetExplorerGroupDto;
import org.example.homework.dto.homework.*;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.model.HomeworkStatusType;
import org.example.homework.repository.*;
import org.example.homework.service.validator.HomeworkValidatorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final PlanetRepository planetRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PersonRepository personRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    private final HomeworkStatusService homeworkStatusService;
    private final PersonService personService;
    private final HomeworkValidatorService homeworkValidatorService;

    @Transactional(readOnly = true)
    public Homework findHomeworkByHomeworkId(Integer homeworkId) {
        return homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
    }

    @Transactional(readOnly = true)
    public List<Homework> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId) {
        homeworkValidatorService.validateGetRequest(themeId, groupId);
        return homeworkRepository.findHomeworksByCourseThemeIdAndGroupId(themeId, groupId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Homework> findHomeworksByHomeworkIdIn(List<Integer> homeworkIds) {
        return homeworkRepository.findAllByHomeworkIdIn(homeworkIds)
                .stream()
                .collect(Collectors.toMap(
                        Homework::getHomeworkId,
                        h -> h
                ));
    }

    @Transactional(readOnly = true)
    public List<Homework> findCompletedHomeworksByThemeIdAndGroupIdForExplorer(Integer themeId, Integer groupId, Integer explorerId) {
        homeworkValidatorService.validateGetCompletedRequest(themeId, groupId, explorerId);
        return homeworkRepository.findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
                themeId, groupId, explorerId
        );
    }

    @Transactional(readOnly = true)
    public List<Homework> findHomeworksByThemeIdForExplorer(Integer themeId) {
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                personService.getAuthenticatedPersonId(),
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId()
        ).orElseThrow(ExplorerNotFoundException::new);
        return homeworkRepository.findHomeworksByCourseThemeIdAndGroupId(
                themeId,
                explorer.getGroupId()
        );
    }

    @Transactional(readOnly = true)
    public GetHomeworksWithRequestsDto findHomeworksByThemeIdForKeeper(Integer themeId) {
        Integer courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();
        Integer keeperId = keeperRepository.findKeeperByPersonIdAndCourseId(
                personService.getAuthenticatedPersonId(),
                courseId
        ).orElseThrow(KeeperNotFoundException::new).getKeeperId();
        List<Integer> groupIds = explorerGroupRepository
                .findExplorerGroupsByKeeperId(keeperId)
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getGroupId)
                .collect(Collectors.toList());
        List<Homework> openedHomeworks = homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupIdInAndStatus_OpenedStatus(
                        themeId,
                        groupIds
                );
        List<Homework> closedHomeworks = homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupIdInAndStatus_ClosedStatus(
                        themeId,
                        groupIds
                );
        return new GetHomeworksWithRequestsDto(
                mapHomeworkToGetHomeworkDto(openedHomeworks),
                mapHomeworkToGetHomeworkDto(closedHomeworks)
        );
    }

    private List<GetHomeworkDto> mapHomeworkToGetHomeworkDto(List<Homework> homeworks) {
        Map<Integer, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupRepository.findExplorerGroupsByGroupIdIn(
                homeworks.stream()
                        .map(Homework::getGroupId)
                        .collect(Collectors.toList())
        );
        Map<Integer, ExplorersService.Explorer> explorers = groups.values()
                .stream()
                .flatMap(g -> g.getExplorersList().stream())
                .collect(Collectors.toMap(
                        ExplorersService.Explorer::getExplorerId,
                        e -> e
                ));
        Map<Integer, PeopleService.Person> people = personRepository.findPeopleByPersonIdIn(
                groups.values()
                        .stream()
                        .flatMap(g -> g.getExplorersList().stream())
                        .map(ExplorersService.Explorer::getPersonId)
                        .collect(Collectors.toList())
        );
        Map<Integer, GetExplorerGroupDto> groupMap = mapGroupsToGetExplorerGroupDto(
                groups, people
        );
        Map<Integer, List<GetHomeworkRequestWithPersonInfoDto>> requests = mapRequestsToGetHomeworkRequestWithPersonInfoDto(
                homeworkRequestRepository.findHomeworkRequestsByHomeworkIdIn(
                        homeworks.stream()
                                .map(Homework::getHomeworkId)
                                .collect(Collectors.toList())
                ),
                explorers,
                people
        );
        return homeworks.stream()
                .map(h -> new GetHomeworkDto(
                        h.getHomeworkId(),
                        h.getCourseThemeId(),
                        h.getContent(),
                        groupMap.get(h.getGroupId()),
                        requests.getOrDefault(h.getHomeworkId(), Collections.emptyList())
                                .stream()
                                .filter(r -> r.getStatus().getStatus().equals(HomeworkRequestStatusType.CHECKING))
                                .count(),
                        requests.getOrDefault(h.getHomeworkId(), Collections.emptyList())
                )).collect(Collectors.toList());
    }

    private Map<Integer, GetExplorerGroupDto> mapGroupsToGetExplorerGroupDto(Map<Integer, ExplorerGroupsService.ExplorerGroup> groups, Map<Integer, PeopleService.Person> people) {
        return groups.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new GetExplorerGroupDto(
                        entry.getValue().getGroupId(),
                        entry.getValue().getKeeperId(),
                        entry.getValue().getCourseId(),
                        entry.getValue().getExplorersList()
                                .stream()
                                .map(e -> {
                                    PeopleService.Person person = people.get(e.getPersonId());
                                    return new ExplorerBaseInfoDto(
                                            person.getPersonId(),
                                            person.getFirstName(),
                                            person.getLastName(),
                                            person.getPatronymic(),
                                            e.getExplorerId()
                                    );
                                }).collect(Collectors.toList())
                )
        ));
    }

    private Map<Integer, List<GetHomeworkRequestWithPersonInfoDto>> mapRequestsToGetHomeworkRequestWithPersonInfoDto(List<HomeworkRequest> requests, Map<Integer, ExplorersService.Explorer> explorers, Map<Integer, PeopleService.Person> people) {
        return requests.stream().collect(Collectors.groupingBy(
                HomeworkRequest::getHomeworkId,
                Collectors.mapping(
                        hr -> {
                            PeopleService.Person person = people.get(
                                    explorers.get(hr.getExplorerId())
                                            .getPersonId()
                            );
                            return new GetHomeworkRequestWithPersonInfoDto(
                                    hr.getRequestId(),
                                    hr.getHomeworkId(),
                                    new ExplorerBaseInfoDto(
                                            person.getPersonId(),
                                            person.getFirstName(),
                                            person.getLastName(),
                                            person.getPatronymic(),
                                            hr.getExplorerId()
                                    ),
                                    hr.getRequestDate(),
                                    hr.getStatus()
                            );
                        }, Collectors.toList()
                )
        ));
    }

    @Transactional
    public Homework addHomework(Integer themeId, CreateHomeworkDto homework) {
        homeworkValidatorService.validatePostRequest(themeId, homework.getGroupId());
        return homeworkRepository.save(
                new Homework(
                        themeId,
                        homework.getContent(),
                        homework.getGroupId(),
                        homeworkStatusService.findHomeworkStatusByStatus(HomeworkStatusType.OPENED).getStatusId()
                )
        );
    }

    @Transactional
    public Homework updateHomework(Integer homeworkId, UpdateHomeworkDto homework) {
        homeworkValidatorService.validatePutRequest(homework);
        Homework updatedHomework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        updatedHomework.setContent(homework.getContent());
        updatedHomework.setCourseThemeId(homework.getCourseThemeId());
        updatedHomework.setGroupId(homework.getGroupId());
        return homeworkRepository.save(updatedHomework);
    }

    @Transactional
    public Map<String, String> deleteHomework(Integer homeworkId) {
        homeworkValidatorService.validateDeleteRequest(homeworkId);
        Map<String, String> response = new HashMap<>();
        homeworkRepository.deleteById(homeworkId);
        response.put("message", "Удалено задание " + homeworkId);
        return response;
    }

    @KafkaListener(topics = "deleteHomeworksTopic", containerFactory = "deleteHomeworksKafkaListenerContainerFactory")
    @Transactional
    public void deleteHomeworksByThemeId(Integer themeId) {
        homeworkRepository.deleteAllByCourseThemeId(themeId);
    }
}
