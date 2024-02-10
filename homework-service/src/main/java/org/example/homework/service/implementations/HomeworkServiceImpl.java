package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.group.GetExplorerGroupDto;
import org.example.homework.dto.homework.*;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.GetHomeworkRequestWithPersonInfoDto;
import org.example.homework.dto.homeworkrequest.HomeworkRequestStatusDto;
import org.example.homework.dto.message.MessageDto;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.homework.HomeworkNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.repository.HomeworkRequestRepository;
import org.example.homework.service.*;
import org.example.homework.service.validator.HomeworkValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    private final CourseProgressService courseProgressService;
    private final PlanetService planetService;
    private final KeeperService keeperService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final PersonService personService;

    private final RoleService roleService;
    private final HomeworkValidatorService homeworkValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public HomeworkDto findHomeworkByHomeworkId(String authorizationHeader, Long authenticatedPersonId, Long homeworkId) {
        if (!(roleService.hasAnyCourseRoleByHomeworkId(authorizationHeader, authenticatedPersonId, homeworkId, CourseRoleType.EXPLORER) ||
                roleService.hasAnyCourseRoleByHomeworkId(authorizationHeader, authenticatedPersonId, homeworkId, CourseRoleType.KEEPER))) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }

        return homeworkRepository.findById(homeworkId)
                .map(h -> mapper.map(h, HomeworkDto.class))
                .orElseThrow(() -> {
                    log.warn("homework by id {} not found", homeworkId);
                    return new HomeworkNotFoundException(homeworkId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(String authorizationHeader, Authentication authentication, Long themeId, Long groupId) {
        homeworkValidatorService.validateGetRequest(authorizationHeader, authentication, themeId, groupId);
        return homeworkRepository
                .findHomeworksByCourseThemeIdAndGroupId(themeId, groupId)
                .stream()
                .map(h -> mapper.map(h, HomeworkDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<HomeworkDto>> findHomeworkByCourseThemeIdInAndGroupId(String authorizationHeader, Long authenticatedPersonId, List<Long> themeIds, Long groupId) {
        homeworkValidatorService.validateGetByCourseThemeIdInRequest(authorizationHeader, authenticatedPersonId, groupId);
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

    @Override
    @Transactional(readOnly = true)
    public Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> homeworkIds) {
        if (!(roleService.hasAnyCourseRoleByHomeworkIds(authorizationHeader, authenticatedPersonId, homeworkIds, CourseRoleType.EXPLORER) ||
                roleService.hasAnyCourseRoleByHomeworkIds(authorizationHeader, authenticatedPersonId, homeworkIds, CourseRoleType.KEEPER))) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }

        return homeworkRepository.findAllByHomeworkIdIn(homeworkIds)
                .stream()
                .collect(Collectors.toMap(
                        Homework::getHomeworkId,
                        h -> mapper.map(h, HomeworkDto.class)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Map<Long, List<HomeworkDto>>> findCompletedHomeworksByCourseThemeIdInAndGroupIdForExplorers(String authorizationHeader, Long authenticatedPersonId, List<Long> themeIds, Long groupId, List<Long> explorerIds) {
        homeworkValidatorService.validateGetCompletedRequest(
                authorizationHeader, authenticatedPersonId, themeIds, groupId, explorerIds
        );

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
            homeworksByThemeIdMap.put(themeId, homeworksByExplorerIdMap);
        }

        return homeworksByThemeIdMap;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeworkDto> findHomeworksByThemeIdForExplorer(String authorizationHeader, Long authenticatedPersonId, Long themeId) {
        ExplorersService.Explorer explorer = explorerService.findExplorerByPersonIdAndGroup_CourseId(
                authorizationHeader, authenticatedPersonId,
                planetService.findById(authorizationHeader, themeId).getSystemId()
        );

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

    @Override
    @Transactional(readOnly = true)
    public GetHomeworksWithRequestsDto findHomeworksByThemeIdForKeeper(String authorizationHeader, Long authenticatedPersonId, Long themeId) {
        Long courseId = planetService.findById(authorizationHeader, themeId).getSystemId();
        Long keeperId = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, authenticatedPersonId, courseId
        ).getKeeperId();
        List<Long> groupIds = explorerGroupService
                .findExplorerGroupsByKeeperId(authorizationHeader, keeperId)
                .stream()
                .map(ExplorerGroupsService.ExplorerGroup::getGroupId)
                .collect(Collectors.toList());

        List<Homework> homeworks = homeworkRepository.findHomeworksByCourseThemeIdAndGroupIdIn(
                themeId, groupIds
        );

        Optional<CurrentKeeperGroupDto> currentGroupOptional = courseProgressService
                .getCurrentGroup(authorizationHeader);

        if (currentGroupOptional.isEmpty()) {
            return new GetHomeworksWithRequestsDto(
                    Collections.emptyList(),
                    mapHomeworkToGetHomeworkDto(authorizationHeader, homeworks)
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
                mapHomeworkToGetHomeworkDto(authorizationHeader, openedHomeworks),
                mapHomeworkToGetHomeworkDto(authorizationHeader, closedHomeworks)
        );
    }

    private List<GetHomeworkDto> mapHomeworkToGetHomeworkDto(String authorizationHeader, List<Homework> homeworks) {
        Map<Long, ExplorerGroupsService.ExplorerGroup> groups = explorerGroupService.findExplorerGroupsByGroupIdIn(
                authorizationHeader,
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
        Map<Long, PeopleService.Person> people = personService.findPeopleByPersonIdIn(
                authorizationHeader,
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

    @Override
    @Transactional
    public Long addHomework(String authorizationHeader, Long authenticatedPersonId, Long themeId, CreateHomeworkDto homework) {
        homeworkValidatorService.validatePostRequest(authorizationHeader, authenticatedPersonId, themeId, homework.getGroupId());

        return homeworkRepository.save(
                new Homework(
                        themeId,
                        homework.getTitle(),
                        homework.getContent(),
                        homework.getGroupId()
                )
        ).getHomeworkId();
    }

    @Override
    @Transactional
    public HomeworkDto updateHomework(String authorizationHeader, Long authenticatedPersonId, Long homeworkId, UpdateHomeworkDto homework) {
        homeworkValidatorService.validatePutRequest(authorizationHeader, authenticatedPersonId, homework);

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

    @Override
    @Transactional
    public MessageDto deleteHomework(String authorizationHeader, Long authenticatedPersonId, Long homeworkId) {
        homeworkValidatorService.validateDeleteRequest(authorizationHeader, authenticatedPersonId, homeworkId);
        homeworkRepository.deleteById(homeworkId);
        return new MessageDto("Удалено задание " + homeworkId);
    }

    @KafkaListener(topics = "deleteHomeworksTopic", containerFactory = "deleteHomeworksKafkaListenerContainerFactory")
    @Transactional
    public void deleteHomeworksByThemeId(Long themeId) {
        homeworkRepository.deleteAllByCourseThemeId(themeId);
    }
}
