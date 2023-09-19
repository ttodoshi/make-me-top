package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExplorerProfileInformationService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerListService explorerListService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseService courseService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerCabinetInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", ratingService.getPersonRatingAsExplorer(authenticatedPersonId));
        List<ExplorerDto> personExplorers = explorerRepository.findExplorersByPersonId(authenticatedPersonId);
        response.put("totalSystems", personExplorers.size());
        courseProgressService.getCurrentCourseProgress(authenticatedPersonId)
                .ifPresent(p -> response.put("currentSystem", p));
        courseRegistrationRequestService.getStudyRequestForExplorerByPersonId(authenticatedPersonId)
                .ifPresent(r -> response.put("studyRequest", r));
        response.put("investigatedSystems", courseService.getCoursesRating(
                courseProgressService.getInvestigatedSystemIds(personExplorers)
        ));
        response.put("ratingTable", explorerListService.getExplorers());
        return response;
    }
}
