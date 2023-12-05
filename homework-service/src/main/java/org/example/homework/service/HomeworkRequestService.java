package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.dto.homework.GetHomeworkWithRequestDto;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.*;
import org.example.homework.dto.keeper.KeeperBaseInfoDto;
import org.example.homework.dto.planet.PlanetDto;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.homework.HomeworkRequestNotFound;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.model.Homework;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final HomeworkRequestVersionRepository homeworkRequestVersionRepository;
    private final HomeworkRequestFeedbackRepository homeworkRequestFeedbackRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;

    private final PersonService personService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public HomeworkRequest findHomeworkRequestById(Long requestId) {
        return homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> new HomeworkRequestNotFound(requestId));
    }

    @Transactional(readOnly = true)
    public List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(List<Long> explorerIds) {
        return homeworkRequestRepository
                .findOpenedHomeworkRequestsByExplorerIdIn(explorerIds)
                .stream()
                .map(hr -> mapper.map(hr, HomeworkRequestDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetHomeworkWithRequestDto findHomeworkWithRequestByHomeworkId(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        PlanetDto planet = planetRepository.findById(homework.getCourseThemeId())
                .orElseThrow(() -> new PlanetNotFoundException(homework.getCourseThemeId()));
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                personService.getAuthenticatedPersonId(),
                planet.getSystemId()
        ).orElseThrow(ExplorerNotFoundException::new);

        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(
                explorer.getGroupId()
        ).orElseThrow(() -> new ExplorerGroupNotFoundException(explorer.getGroupId()));

        KeepersService.Keeper keeper = keeperRepository.findById(
                explorerGroup.getKeeperId()
        ).orElseThrow(KeeperNotFoundException::new);

        PeopleService.Person explorerPerson = personService.getAuthenticatedPerson();
        PeopleService.Person keeperPerson = personService
                .findPersonById(keeper.getPersonId());

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
                homework.getContent(),
                homeworkRequest.orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public GetHomeworkWithRequestDto findHomeworkWithRequestByRequestId(Long requestId) {
        HomeworkRequest homeworkRequest = homeworkRequestRepository.findById(requestId)
                .orElseThrow(() -> new HomeworkRequestNotFound(requestId));
        Homework homework = homeworkRepository.findById(homeworkRequest.getHomeworkId())
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkRequest.getHomeworkId()));
        PlanetDto planet = planetRepository.findById(homework.getCourseThemeId())
                .orElseThrow(() -> new PlanetNotFoundException(homework.getCourseThemeId()));

        KeepersService.Keeper keeper = keeperRepository.findKeeperByPersonIdAndCourseId(
                personService.getAuthenticatedPersonId(),
                planet.getSystemId()
        ).orElseThrow(KeeperNotFoundException::new);
        ExplorersService.Explorer explorer = explorerRepository.findById(homeworkRequest.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(homeworkRequest.getExplorerId()));

        PeopleService.Person keeperPerson = personService.getAuthenticatedPerson();
        PeopleService.Person explorerPerson = personService
                .findPersonById(explorer.getPersonId());

        return new GetHomeworkWithRequestDto(
                homework.getHomeworkId(),
                homework.getContent(),
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
                mapper.map(homeworkRequest.getMark(), HomeworkMarkDto.class)
        );
    }
}
