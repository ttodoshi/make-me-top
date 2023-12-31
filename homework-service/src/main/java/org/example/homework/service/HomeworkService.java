package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.group.GetExplorerGroupDto;
import org.example.homework.dto.homework.*;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.GetHomeworkRequestWithPersonInfoDto;
import org.example.homework.dto.homeworkrequest.HomeworkRequestStatusDto;
import org.example.homework.dto.message.MessageDto;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.*;
import org.example.homework.service.validator.HomeworkValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final CourseProgressRepository courseProgressRepository;

    private final PersonService personService;
    private final HomeworkValidatorService homeworkValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public HomeworkDto findHomeworkByHomeworkId(Long homeworkId) {
        return homeworkRepository.findById(homeworkId)
                .map(h -> mapper.map(h, HomeworkDto.class))
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId) {
        homeworkValidatorService.validateGetRequest(themeId, groupId);
        return homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupId(themeId, groupId)
                .stream()
                .map(h -> mapper.map(h, HomeworkDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<Long, List<HomeworkDto>> findHomeworkByCourseThemeIdInAndGroupId(List<Long> themeIds, Long groupId) {
        homeworkValidatorService.validateGetByCourseThemeIdInRequest(groupId);
        return homeworkRepository
                .findHomeworksByCourseThemeIdInAndGroupId(themeIds, groupId)
                .stream()
                .collect(Collectors.groupingBy(
                        Homework::getCourseThemeId,
                        Collectors.mapping(
                                h -> mapper.map(h, HomeworkDto.class),
                                Collectors.toList()
                        )
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(List<Long> homeworkIds) {
        return homeworkRepository.findAllByHomeworkIdIn(homeworkIds)
                .stream()
                .collect(Collectors.toMap(
                        Homework::getHomeworkId,
                        h -> mapper.map(h, HomeworkDto.class)
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<Long, List<HomeworkDto>>> findCompletedHomeworksByCourseThemeIdInAndGroupIdForExplorers(List<Long> themeIds, Long groupId, List<Long> explorerIds) {
        homeworkValidatorService.validateGetCompletedRequest(themeIds, groupId, explorerIds);

        Map<Long, Map<Long, List<HomeworkDto>>> homeworksByThemeIdMap = new HashMap<>();

        for (Long themeId : themeIds) {
            Map<Long, List<HomeworkDto>> homeworksByExplorerIdMap = new HashMap<>();
            for (Long explorerId : explorerIds) {
                homeworksByExplorerIdMap.put(
                        explorerId,
                        homeworkRepository
                                .findAllCompletedByCourseThemeIdAndGroupIdForExplorer(
                                        themeId, groupId, explorerId
                                ).stream()
                                .map(h -> mapper.map(h, HomeworkDto.class))
                                .collect(Collectors.toList())
                );
            }
            homeworksByThemeIdMap.put(
                    themeId,
                    homeworksByExplorerIdMap
            );
        }

        return homeworksByThemeIdMap;
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> findHomeworksByThemeIdForExplorer(Long themeId) {
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                personService.getAuthenticatedPersonId(),
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId()
        ).orElseThrow(ExplorerNotFoundException::new);

        return homeworkRepository.findHomeworksByCourseThemeIdAndGroupId(
                        themeId,
                        explorer.getGroupId()
                ).stream()
                .map(h -> {
                    Optional<HomeworkRequest> request = homeworkRequestRepository
                            .findHomeworkRequestByHomeworkIdAndExplorerId(
                                    h.getHomeworkId(),
                                    explorer.getExplorerId()
                            );
                    if (request.isPresent()) {
                        return new GetHomeworkWithMarkDto(
                                mapper.map(h, HomeworkDto.class),
                                mapper.map(request.get().getStatus(), HomeworkRequestStatusDto.class),
                                request.get().getMark() == null ? null : mapper.map(request.get().getMark(), HomeworkMarkDto.class)
                        );
                    }
                    return mapper.map(h, HomeworkDto.class);
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetHomeworksWithRequestsDto findHomeworksByThemeIdForKeeper(Long themeId) {
        Long courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();
        Long keeperId = keeperRepository.findKeeperByPersonIdAndCourseId(
                personService.getAuthenticatedPersonId(),
                courseId
        ).orElseThrow(KeeperNotFoundException::new).getKeeperId();
        List<Long> groupIds = explorerGroupRepository
                .findExplorerGroupsByKeeperId(keeperId)
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getGroupId)
                .collect(Collectors.toList());

        List<Homework> homeworks = homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupIdIn(
                        themeId,
                        groupIds
                );

        Optional<CurrentKeeperGroupDto> currentGroupOptional = courseProgressRepository.getCurrentGroup();

        if (currentGroupOptional.isEmpty()) {
            return new GetHomeworksWithRequestsDto(
                    Collections.emptyList(),
                    mapHomeworkToGetHomeworkDto(homeworks)
            );
        }

        List<Homework> openedHomeworks = new ArrayList<>();
        List<Homework> closedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            if (homework.getGroupId().equals(currentGroupOptional.get().getGroupId())) {
                openedHomeworks.add(homework);
            } else {
                closedHomeworks.add(homework);
            }
        }

        return new GetHomeworksWithRequestsDto(
                mapHomeworkToGetHomeworkDto(openedHomeworks),
                mapHomeworkToGetHomeworkDto(closedHomeworks)
        );
    }

    private List<GetHomeworkDto> mapHomeworkToGetHomeworkDto(List<Homework> homeworks) {
        Map<Long, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupRepository.findExplorerGroupsByGroupIdIn(
                homeworks.stream()
                        .map(Homework::getGroupId)
                        .collect(Collectors.toList())
        );
        Map<Long, ExplorersService.Explorer> explorers = groups.values()
                .stream()
                .flatMap(g -> g.getExplorersList().stream())
                .collect(Collectors.toMap(
                        ExplorersService.Explorer::getExplorerId,
                        e -> e
                ));
        Map<Long, PeopleService.Person> people = personRepository.findPeopleByPersonIdIn(
                groups.values()
                        .stream()
                        .flatMap(g -> g.getExplorersList().stream())
                        .map(ExplorersService.Explorer::getPersonId)
                        .collect(Collectors.toList())
        );
        Map<Long, GetExplorerGroupDto> groupMap = mapGroupsToGetExplorerGroupDto(
                groups, people
        );
        Map<Long, List<GetHomeworkRequestWithPersonInfoDto>> requests = mapRequestsToGetHomeworkRequestWithPersonInfoDto(
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
                        h.getTitle(),
                        h.getContent(),
                        groupMap.get(h.getGroupId()),
                        requests.getOrDefault(h.getHomeworkId(), Collections.emptyList())
                                .stream()
                                .filter(r -> r.getStatus().getStatus().equals(HomeworkRequestStatusType.CHECKING))
                                .count(),
                        requests.getOrDefault(h.getHomeworkId(), Collections.emptyList())
                )).collect(Collectors.toList());
    }

    private Map<Long, GetExplorerGroupDto> mapGroupsToGetExplorerGroupDto(Map<Long, ExplorerGroupsService.ExplorerGroup> groups, Map<Long, PeopleService.Person> people) {
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

    private Map<Long, List<GetHomeworkRequestWithPersonInfoDto>> mapRequestsToGetHomeworkRequestWithPersonInfoDto(List<HomeworkRequest> requests, Map<Long, ExplorersService.Explorer> explorers, Map<Long, PeopleService.Person> people) {
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
    public Long addHomework(Long themeId, CreateHomeworkDto homework) {
        homeworkValidatorService.validatePostRequest(themeId, homework.getGroupId());

        return homeworkRepository.save(
                new Homework(
                        themeId,
                        homework.getTitle(),
                        homework.getContent(),
                        homework.getGroupId()
                )
        ).getHomeworkId();
    }

    @Transactional
    public HomeworkDto updateHomework(Long homeworkId, UpdateHomeworkDto homework) {
        homeworkValidatorService.validatePutRequest(homework);

        Homework updatedHomework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        updatedHomework.setTitle(homework.getTitle());
        updatedHomework.setContent(homework.getContent());
        updatedHomework.setCourseThemeId(homework.getCourseThemeId());
        updatedHomework.setGroupId(homework.getGroupId());

        return mapper.map(
                homeworkRepository.save(updatedHomework),
                HomeworkDto.class
        );
    }

    @Transactional
    public MessageDto deleteHomework(Long homeworkId) {
        homeworkValidatorService.validateDeleteRequest(homeworkId);
        homeworkRepository.deleteById(homeworkId);
        return new MessageDto("Удалено задание " + homeworkId);
    }

    @KafkaListener(topics = "deleteHomeworksTopic", containerFactory = "deleteHomeworksKafkaListenerContainerFactory")
    @Transactional
    public void deleteHomeworksByThemeId(Long themeId) {
        homeworkRepository.deleteAllByCourseThemeId(themeId);
    }
}
