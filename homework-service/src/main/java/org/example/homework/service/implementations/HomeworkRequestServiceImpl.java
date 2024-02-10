package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.dto.homework.GetHomeworkWithRequestDto;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.*;
import org.example.homework.dto.keeper.KeeperBaseInfoDto;
import org.example.homework.dto.planet.PlanetDto;
import org.example.homework.enums.CourseRoleType;
import org.example.homework.exception.homework.HomeworkNotFoundException;
import org.example.homework.exception.homework.HomeworkRequestNotFound;
import org.example.homework.model.Homework;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.repository.HomeworkRequestFeedbackRepository;
import org.example.homework.repository.HomeworkRequestRepository;
import org.example.homework.repository.HomeworkRequestVersionRepository;
import org.example.homework.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkRequestServiceImpl implements HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkRequestVersionRepository homeworkRequestVersionRepository;
    private final HomeworkRequestFeedbackRepository homeworkRequestFeedbackRepository;
    private final HomeworkRepository homeworkRepository;

    private final RoleService roleService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final ExplorerGroupService explorerGroupService;
    private final PlanetService planetService;
    private final PersonService personService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public HomeworkRequest findHomeworkRequestById(Long requestId) {
        return homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("homework request by id {} not found", requestId);
                    return new HomeworkRequestNotFound(requestId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds) {
        if (!(roleService.hasAnyCourseRoleByExplorerIds(authorizationHeader, authenticatedPersonId, explorerIds, CourseRoleType.EXPLORER) ||
                roleService.hasAnyCourseRoleByExplorerIds(authorizationHeader, authenticatedPersonId, explorerIds, CourseRoleType.KEEPER))) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        return homeworkRequestRepository
                .findOpenedHomeworkRequestsByExplorerIdIn(explorerIds)
                .stream()
                .map(hr -> mapper.map(hr, HomeworkRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GetHomeworkWithRequestDto findHomeworkWithRequestByHomeworkId(String authorizationHeader, Long authenticatedPersonId, Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> {
                    log.warn("homework by id {} not found", homeworkId);
                    return new HomeworkNotFoundException(homeworkId);
                });
        PlanetDto planet = planetService.findById(authorizationHeader, homework.getCourseThemeId());
        ExplorersService.Explorer explorer = explorerService.findExplorerByPersonIdAndGroup_CourseId(
                authorizationHeader, authenticatedPersonId, planet.getSystemId()
        );
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(
                authorizationHeader, explorer.getGroupId()
        );
        KeepersService.Keeper keeper = keeperService.findById(
                authorizationHeader, explorerGroup.getKeeperId()
        );

        PeopleService.Person explorerPerson = personService.findPersonById(authorizationHeader, authenticatedPersonId);
        PeopleService.Person keeperPerson = personService.findPersonById(authorizationHeader, keeper.getPersonId());

        Optional<GetHomeworkRequestWithVersionsDto> homeworkRequest = homeworkRequestRepository.findHomeworkRequestByHomeworkIdAndExplorerId(
                homeworkId,
                explorer.getExplorerId()
        ).map(hr -> mapHomeworkRequest(
                hr,
                new ExplorerBaseInfoDto(
                        explorerPerson.getPersonId(),
                        explorerPerson.getFirstName(),
                        explorerPerson.getLastName(),
                        explorerPerson.getPatronymic(),
                        explorer.getExplorerId()
                ),
                new KeeperBaseInfoDto(
                        keeperPerson.getPersonId(),
                        keeperPerson.getFirstName(),
                        keeperPerson.getLastName(),
                        keeperPerson.getPatronymic(),
                        keeper.getKeeperId()
                )
        ));

        return new GetHomeworkWithRequestDto(
                homework.getHomeworkId(),
                homework.getTitle(),
                homework.getContent(),
                homeworkRequest.orElse(null)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GetHomeworkWithRequestDto findHomeworkWithRequestByRequestId(String authorizationHeader, Long authenticatedPersonId, Long requestId) {
        HomeworkRequest homeworkRequest = homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("homework request by id {} not found", requestId);
                    return new HomeworkRequestNotFound(requestId);
                });
        PlanetDto planet = planetService.findById(authorizationHeader, homeworkRequest.getHomework().getCourseThemeId());
        KeepersService.Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, authenticatedPersonId, planet.getSystemId()
        );
        ExplorersService.Explorer explorer = explorerService.findById(authorizationHeader, homeworkRequest.getExplorerId());

        PeopleService.Person keeperPerson = personService.findPersonById(authorizationHeader, authenticatedPersonId);
        PeopleService.Person explorerPerson = personService.findPersonById(authorizationHeader, explorer.getPersonId());

        return new GetHomeworkWithRequestDto(
                homeworkRequest.getHomeworkId(),
                homeworkRequest.getHomework().getTitle(),
                homeworkRequest.getHomework().getContent(),
                mapHomeworkRequest(
                        homeworkRequest,
                        new ExplorerBaseInfoDto(
                                explorerPerson.getPersonId(),
                                explorerPerson.getFirstName(),
                                explorerPerson.getLastName(),
                                explorerPerson.getPatronymic(),
                                explorer.getExplorerId()
                        ),
                        new KeeperBaseInfoDto(
                                keeperPerson.getPersonId(),
                                keeperPerson.getFirstName(),
                                keeperPerson.getLastName(),
                                keeperPerson.getPatronymic(),
                                keeper.getKeeperId()
                        )
                )
        );
    }

    private GetHomeworkRequestWithVersionsDto mapHomeworkRequest(
            HomeworkRequest homeworkRequest,
            ExplorerBaseInfoDto explorer,
            KeeperBaseInfoDto keeper) {
        return new GetHomeworkRequestWithVersionsDto(
                homeworkRequest.getRequestId(),
                homeworkRequest.getHomeworkId(),
                homeworkRequest.getExplorerId(),
                homeworkRequest.getRequestDate(),
                mapper.map(homeworkRequest.getStatus(), HomeworkRequestStatusDto.class),
                homeworkRequestVersionRepository
                        .findHomeworkRequestVersionsByRequestIdOrderByCreationDateDesc(
                                homeworkRequest.getRequestId()
                        ).stream()
                        .map(v -> new GetHomeworkRequestVersionDto(
                                v.getVersionId(),
                                v.getRequestId(),
                                v.getContent(),
                                v.getCreationDate(),
                                explorer,
                                homeworkRequestFeedbackRepository
                                        .findHomeworkRequestFeedbacksByRequestVersionIdOrderByCreationDateDesc(
                                                v.getVersionId()
                                        ).stream()
                                        .map(f -> new GetHomeworkRequestFeedbackDto(
                                                f.getFeedbackId(),
                                                f.getRequestVersionId(),
                                                f.getComment(),
                                                f.getCreationDate(),
                                                keeper
                                        )).collect(Collectors.toList())
                        )).collect(Collectors.toList()),
                homeworkRequest.getMark() == null ? null : mapper.map(homeworkRequest.getMark(), HomeworkMarkDto.class)
        );
    }

    @KafkaListener(topics = "deleteHomeworkRequestTopic", containerFactory = "deleteHomeworkRequestByExplorerIdKafkaListenerContainerFactory")
    @Transactional
    public void deleteHomeworkRequestsByExplorerId(Long explorerId) {
        homeworkRequestRepository.deleteHomeworkRequestsByExplorerId(explorerId);
    }
}
