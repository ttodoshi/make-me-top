package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.courserequest.*;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.keeper.KeeperBasicInfoDto;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.repository.*;
import org.example.person.service.CourseRegistrationRequestService;
import org.example.person.service.KeeperService;
import org.example.person.service.PersonService;
import org.example.person.service.RatingService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestServiceImpl implements CourseRegistrationRequestService {
    private final PersonRepository personRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final GalaxyRepository galaxyRepository;

    private final PersonService personService;
    private final KeeperService keeperService;
    private final RatingService ratingService;

    @Override
    public List<CourseRegistrationRequestsForKeeperDto> getStudyRequestsForKeeper(List<Keeper> keepers) {
        List<CourseRegistrationRequestKeeperDto> openedRequests = courseRegistrationRequestKeeperRepository
                .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(
                        keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
                );
        Map<Integer, CourseRegistrationRequestDto> requests = courseRegistrationRequestRepository.findCourseRegistrationRequestsByRequestIdIn(
                openedRequests.stream().map(CourseRegistrationRequestKeeperDto::getRequestId).collect(Collectors.toList())
        );

        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                requests.values().stream().map(CourseRegistrationRequestDto::getCourseId).collect(Collectors.toList())
        );
        Map<Integer, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                requests.values().stream().map(CourseRegistrationRequestDto::getPersonId).collect(Collectors.toList())
        );

        return openedRequests.stream()
                .map(kr -> {
                    CourseRegistrationRequestDto currentRequest = requests.get(kr.getRequestId());
                    Person person = personService.findPersonById(currentRequest.getPersonId());
                    return new CourseRegistrationRequestsForKeeperDto.CourseRegistrationRequestForKeeperDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            currentRequest.getCourseId(),
                            courses.get(currentRequest.getCourseId()).getTitle(),
                            currentRequest.getRequestId(),
                            currentRequest.getRequestDate(),
                            kr.getKeeperId(),
                            ratings.get(person.getPersonId())
                    );
                }).collect(Collectors.groupingBy(
                        r -> new AbstractMap.SimpleEntry<>(r.getCourseId(), r.getCourseTitle()),
                        Collectors.mapping(Function.identity(), Collectors.toList())
                )).entrySet().stream()
                .map(e -> {
                    Collections.sort(e.getValue());
                    return new CourseRegistrationRequestsForKeeperDto(
                            e.getKey().getKey(),
                            e.getKey().getValue(),
                            e.getValue()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId() {
        return courseRegistrationRequestRepository
                .findProcessingCourseRegistrationRequestByPersonId()
                .map(r -> {
                    CourseDto course = courseRepository.getReferenceById(r.getCourseId());
                    GalaxyDto galaxy = galaxyRepository.findGalaxyBySystemId(r.getCourseId());

                    List<Integer> keeperIds = courseRegistrationRequestKeeperRepository
                            .findCourseRegistrationRequestKeepersByRequestId(r.getRequestId())
                            .stream()
                            .map(CourseRegistrationRequestKeeperDto::getKeeperId)
                            .collect(Collectors.toList());
                    List<KeeperBasicInfoDto> keepers = keeperService
                            .findKeepersByKeeperIdIn(keeperIds)
                            .entrySet()
                            .stream()
                            .map(e -> {
                                Person person = personRepository.getReferenceById(e.getValue().getPersonId());
                                return new KeeperBasicInfoDto(
                                        person.getPersonId(),
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getPatronymic(),
                                        e.getKey()
                                );
                            }).collect(Collectors.toList());

                    return new CourseRegistrationRequestForExplorerDto(
                            r.getRequestId(),
                            r.getCourseId(),
                            course.getTitle(),
                            galaxy.getGalaxyId(),
                            galaxy.getGalaxyName(),
                            keepers
                    );
                });
    }

    @Override
    public Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByExplorerPersonId(Integer authenticatedPersonId, Integer personId) {
        // returns information about the request only if the authorized keeper is the one to whom it was sent
        return getStudyRequestsForKeeper(
                keeperRepository.findKeepersByPersonId(authenticatedPersonId)
        ).stream()
                .flatMap(requests -> requests.getRequests().stream())
                .filter(r -> r.getPersonId().equals(personId))
                .findAny()
                .map(r -> {
                    GalaxyDto galaxy = galaxyRepository.findGalaxyBySystemId(r.getCourseId());
                    return new CourseRegistrationRequestForKeeperWithGalaxyDto(
                            r.getRequestId(),
                            r.getPersonId(),
                            r.getFirstName(),
                            r.getLastName(),
                            r.getPatronymic(),
                            r.getCourseId(),
                            r.getCourseTitle(),
                            ratingService.getPersonRatingAsExplorer(personId),
                            galaxy.getGalaxyId(),
                            galaxy.getGalaxyName()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetApprovedCourseRegistrationRequestsForKeeperDto> getApprovedRequestsForKeeper(List<Keeper> keepers) {
        List<ApprovedRequestDto> approvedRequests =
                courseRegistrationRequestRepository.getApprovedCourseRegistrationRequests(
                        keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
                );

        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(
                approvedRequests.stream().map(ApprovedRequestDto::getCourseId).collect(Collectors.toList())
        );
        Map<Integer, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                approvedRequests.stream().map(ApprovedRequestDto::getPersonId).collect(Collectors.toList())
        );

        return approvedRequests.stream()
                .map(r -> {
                    Person person = personService.findPersonById(r.getPersonId());
                    return new GetApprovedCourseRegistrationRequestsForKeeperDto.ApprovedCourseRegistrationRequestDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            r.getCourseId(),
                            courses.get(r.getCourseId()).getTitle(),
                            r.getRequestId(),
                            r.getResponseDate(),
                            r.getKeeperId(),
                            ratings.get(person.getPersonId())
                    );
                }).collect(Collectors.groupingBy(
                        r -> new AbstractMap.SimpleEntry<>(r.getCourseId(), r.getCourseTitle()),
                        Collectors.mapping(Function.identity(), Collectors.toList())
                )).entrySet().stream()
                .map(e -> {
                    Collections.sort(e.getValue());
                    return new GetApprovedCourseRegistrationRequestsForKeeperDto(
                            e.getKey().getKey(),
                            e.getKey().getValue(),
                            e.getValue()
                    );
                }).collect(Collectors.toList());
    }
}
