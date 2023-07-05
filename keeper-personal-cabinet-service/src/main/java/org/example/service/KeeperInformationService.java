package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Person;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.HomeworkRequestRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeeperInformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    public Map<String, Object> getKeeperInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("keeper", authenticatedPerson);
        response.put("rating", getKeeperRating(authenticatedPersonId));
        response.put("totalSystems", keeperRepository.getKeeperSystems(authenticatedPersonId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(authenticatedPersonId));
        response.put("studyingExplorers", explorerRepository.getStudyingPeopleByKeeperPersonId(authenticatedPersonId));
        response.put("studyRequests", courseRegistrationRequestRepository.getStudyRequestsByKeeperPersonId(authenticatedPersonId));
        response.put("finalAssessments", explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(authenticatedPersonId));
        response.put("reviewRequests", homeworkRequestRepository.getReviewRequestsByKeeperPersonId(authenticatedPersonId));
        return response;
    }

    // TODO
    private Integer getKeeperRating(Integer personId) {
        return null;
    }
}
