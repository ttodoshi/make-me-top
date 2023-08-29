package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeper;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.homework.HomeworkRequestRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperCabinetInformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperCabinetInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", ratingService.getKeeperRating(authenticatedPersonId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(authenticatedPersonId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(authenticatedPersonId));
        response.put("studyingExplorers", explorerRepository.getStudyingPeopleByKeeperPersonId(authenticatedPersonId));
        response.put("studyRequests", getStudyRequestsForKeeper(authenticatedPersonId));
        response.put("finalAssessments", explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(authenticatedPersonId));
        response.put("reviewRequests", homeworkRequestRepository.getReviewRequestsByKeeperPersonId(authenticatedPersonId));
        return response;
    }

    private List<CourseRegistrationRequestForKeeper> getStudyRequestsForKeeper(Integer personId) {
        return courseRegistrationRequestRepository
                .getStudyRequestsByKeeperPersonId(personId)
                .stream()
                .peek(
                        r -> r.setRating(ratingService.getExplorerRating(r.getPersonId()))
                )
                .collect(Collectors.toList());
    }
}
