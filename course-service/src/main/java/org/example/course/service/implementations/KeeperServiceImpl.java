package org.example.course.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.course.exception.keeper.KeeperNotFoundException;
import org.example.course.service.*;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperServiceImpl implements KeeperService {
    private final PersonService personService;
    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final RatingService ratingService;

    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    public KeepersService.Keeper findById(String authorizationHeader, Long keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return keeperServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findKeeperById(
                            KeepersService.KeeperByIdRequest.newBuilder()
                                    .setKeeperId(keeperId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("keeper by id {} not found", keeperId);
            throw new KeeperNotFoundException();
        }
    }

    @Override
    public List<KeepersService.Keeper> findKeepersByCourseId(String authorizationHeader, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findKeepersByCourseId(
                        KeepersService.KeepersByCourseIdRequest.newBuilder()
                                .setCourseId(courseId)
                                .build()
                ).getKeepersList();
    }

    @Override
    public Boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .existsKeeperByPersonIdAndCourseId(
                        KeepersService.KeeperByPersonIdAndCourseIdRequest.newBuilder()
                                .setPersonId(personId)
                                .setCourseId(courseId)
                                .build()
                ).getKeeperExists();
    }

    @Override
    public Map<Long, KeeperWithRatingDto> getKeepersForCourse(String authorizationHeader, Long courseId) {
        List<KeepersService.Keeper> keepers = findKeepersByCourseId(authorizationHeader, courseId);
        Map<Long, PeopleService.Person> people = personService.findPeopleByPersonIdIn(
                authorizationHeader,
                keepers.stream().map(KeepersService.Keeper::getPersonId).collect(Collectors.toList())
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
                authorizationHeader,
                keepers.stream().map(KeepersService.Keeper::getPersonId).collect(Collectors.toList())
        );
        return keepers.stream().collect(Collectors.toMap(
                KeepersService.Keeper::getPersonId,
                k -> {
                    PeopleService.Person currentKeeperPerson = people.get(k.getPersonId());
                    return new KeeperWithRatingDto(
                            currentKeeperPerson.getPersonId(),
                            currentKeeperPerson.getFirstName(),
                            currentKeeperPerson.getLastName(),
                            currentKeeperPerson.getPatronymic(),
                            k.getKeeperId(),
                            ratings.get(k.getPersonId())
                    );
                }
        ));
    }

    @Override
    public KeepersService.Keeper getKeeperForExplorer(String authorizationHeader, Long explorerId) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(
                authorizationHeader,
                explorerService.findById(authorizationHeader, explorerId).getGroupId()
        );
        return findById(
                authorizationHeader, explorerGroup.getKeeperId()
        );
    }
}
