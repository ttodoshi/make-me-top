package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.dto.courseprogress.CurrentCourseProgressDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.dto.feedback.KeeperFeedbackDto;
import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.homework.HomeworkRequestDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.example.repository.courseprogress.CourseMarkRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.custom.GalaxyRepository;
import org.example.repository.feedback.KeeperFeedbackRepository;
import org.example.repository.homework.HomeworkRequestRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerPublicInformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final CourseMarkRepository courseMarkRepository;
    private final PersonRepository personRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final GalaxyRepository galaxyRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;

    private final RatingService ratingService;
    private final CourseProgressServiceImpl courseProgressServiceImpl;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerPublicInformation(Integer personId) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", ratingService.getExplorerRating(personId));
        List<KeeperFeedbackDto> feedback = keeperFeedbackRepository
                .getExplorerCommentsByPersonId(personId);
        response.put("totalFeedback", feedback.size());
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(personId));
        Optional<CurrentCourseProgressDto> currentCourseOptional = courseProgressServiceImpl
                .getCurrentCourseProgress(personId);
        if (currentCourseOptional.isEmpty()) {
            courseRegistrationRequestRepository.getStudyRequestByPersonId(personId).filter(
                    r -> requestIsForAuthenticatedKeeper(authenticatedPersonId, r)
            ).ifPresent(
                    r -> response.put("studyRequest", getStudyRequestForKeeperWithGalaxy(r))
            );
        } else {
            if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.KEEPER)) {
                getHomeworkRequestForKeeperFromPerson(authenticatedPersonId, personId).ifPresent(
                        hr -> response.put("reviewRequest", hr)
                );
            }
            response.put("currentSystem", currentCourseOptional.get());
        }
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(personId));
        response.put("feedback", feedback);
        return response;
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private boolean requestIsForAuthenticatedKeeper(Integer personId, CourseRegistrationRequestForKeeperWithGalaxyDto studyRequest) {
        return courseRegistrationRequestKeeperRepository
                .findAllByRequestId(studyRequest.getRequestId())
                .stream().map(k -> keeperRepository.getReferenceById(k.getKeeperId()))
                .map(Keeper::getPersonId)
                .collect(Collectors.toList())
                .contains(personId);
    }

    private CourseRegistrationRequestForKeeperWithGalaxyDto getStudyRequestForKeeperWithGalaxy(CourseRegistrationRequestForKeeperWithGalaxyDto request) {
        request.setRating(ratingService.getExplorerRating(request.getPersonId()));
        GalaxyDto galaxy = galaxyRepository.getGalaxyBySystemId(request.getCourseId());
        request.setGalaxyId(galaxy.getGalaxyId());
        request.setGalaxyName(galaxy.getGalaxyName());
        return request;
    }

    private Optional<HomeworkRequestDto> getHomeworkRequestForKeeperFromPerson(Integer keeperPersonId, Integer personId) {
        List<HomeworkRequestDto> homeworkRequests = homeworkRequestRepository
                .getReviewRequestsByKeeperPersonId(keeperPersonId)
                .stream()
                .filter(h -> h.getPersonId().equals(personId))
                .collect(Collectors.toList());
        Optional<HomeworkRequestDto> homeworkRequestOptional = Optional.empty();
        if (!homeworkRequests.isEmpty()) {
            homeworkRequestOptional = homeworkRequests
                    .stream()
                    .filter(hr -> hr.getPersonId().equals(personId))
                    .findFirst();
        }
        return homeworkRequestOptional;
    }
}
