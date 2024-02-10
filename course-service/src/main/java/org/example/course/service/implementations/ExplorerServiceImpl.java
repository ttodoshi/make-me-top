package org.example.course.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.exception.explorer.ExplorerNotFoundException;
import org.example.course.service.ExplorerService;
import org.example.course.service.PersonService;
import org.example.course.service.RatingService;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerServiceImpl implements ExplorerService {
    private final PersonService personService;
    private final RatingService ratingService;

    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Map<Long, ExplorerWithRatingDto> getExplorersForCourse(String authorizationHeader, Long courseId) {
        List<ExplorersService.Explorer> explorers = findExplorersByCourseId(authorizationHeader, courseId);
        Map<Long, PeopleService.Person> people = personService.findPeopleByPersonIdIn(
                authorizationHeader,
                explorers.stream().map(ExplorersService.Explorer::getPersonId).collect(Collectors.toList())
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                authorizationHeader,
                explorers.stream().map(ExplorersService.Explorer::getPersonId).collect(Collectors.toList())
        );
        return explorers.stream().collect(Collectors.toMap(
                ExplorersService.Explorer::getPersonId,
                e -> {
                    PeopleService.Person currentExplorerPerson = people.get(e.getPersonId());
                    return new ExplorerWithRatingDto(
                            currentExplorerPerson.getPersonId(),
                            currentExplorerPerson.getFirstName(),
                            currentExplorerPerson.getLastName(),
                            currentExplorerPerson.getPatronymic(),
                            e.getExplorerId(),
                            ratings.get(e.getPersonId())
                    );
                }
        ));
    }

    @Override
    public ExplorersService.Explorer findById(String authorizationHeader, Long explorerId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return explorerServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findExplorerById(
                            ExplorersService.ExplorerByIdRequest.newBuilder()
                                    .setExplorerId(explorerId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("explorer by id {} not found", explorerId);
            throw new ExplorerNotFoundException(explorerId);
        }
    }

    @Override
    public ExplorersService.Explorer findExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return explorerServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findExplorerByPersonIdAndGroupCourseId(
                            ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest.newBuilder()
                                    .setPersonId(personId)
                                    .setCourseId(courseId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("explorer by person id {} and course id {} not found", personId, courseId);
            throw new ExplorerNotFoundException();
        }
    }

    @Override
    public List<ExplorersService.Explorer> findExplorersByCourseId(String authorizationHeader, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByGroupCourseId(
                        ExplorersService.ExplorersByGroup_CourseIdRequest.newBuilder()
                                .setCourseId(courseId)
                                .build()
                ).getExplorersList();
    }

    @Override
    public Boolean existsExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .existsExplorerByPersonIdAndGroupCourseId(
                        ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest.newBuilder()
                                .setPersonId(personId)
                                .setCourseId(courseId)
                                .build()
                ).getExplorerExists();
    }
}
