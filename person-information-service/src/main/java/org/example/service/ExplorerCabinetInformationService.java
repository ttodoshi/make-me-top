package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestForExplorer;
import org.example.dto.feedback.PersonWithRating;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.keeper.KeeperDTO;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.example.repository.courseprogress.CourseMarkRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.custom.GalaxyRepository;
import org.example.repository.feedback.KeeperFeedbackRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExplorerCabinetInformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseMarkRepository courseMarkRepository;
    private final PersonRepository personRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final GalaxyRepository galaxyRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;

    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerCabinetInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", ratingService.getExplorerRating(authenticatedPersonId));
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(authenticatedPersonId));
        courseProgressService.getCurrentCourseProgress(authenticatedPersonId)
                .ifPresent(p -> response.put("currentSystem", p));
        courseRegistrationRequestRepository.getStudyRequestByExplorerPersonId(authenticatedPersonId)
                .ifPresent(r -> response.put("studyRequest", getStudyRequestForExplorer(r)));
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(authenticatedPersonId));
        response.put("ratingTable", getRatingTable());
        return response;
    }

    private void setKeeperForStudyRequest(CourseRegistrationRequestForExplorer studyRequest) {
        List<CourseRegistrationRequestKeeper> courseRegistrationRequestKeepers = courseRegistrationRequestKeeperRepository.findAllByRequestId(studyRequest.getRequestId());
        if (courseRegistrationRequestKeepers.size() == 1) {
            Integer keeperId = courseRegistrationRequestKeepers.get(0).getKeeperId();
            Keeper keeper = keeperRepository.getReferenceById(keeperId);
            Person person = personRepository.getReferenceById(keeper.getPersonId());
            studyRequest.setKeeper(
                    new KeeperDTO(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            keeperId)
            );
        }
    }

    private CourseRegistrationRequestForExplorer getStudyRequestForExplorer(CourseRegistrationRequestForExplorer request) {
        setKeeperForStudyRequest(request);
        GalaxyDTO galaxy = galaxyRepository.getGalaxyBySystemId(request.getCourseId());
        request.setGalaxyId(galaxy.getGalaxyId());
        request.setGalaxyName(galaxy.getGalaxyName());
        return request;
    }

    private List<PersonWithRating> getRatingTable() {
        return keeperFeedbackRepository.getRatingTable();
    }
}
