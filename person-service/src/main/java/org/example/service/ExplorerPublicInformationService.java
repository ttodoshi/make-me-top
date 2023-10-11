package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.model.Explorer;
import org.example.dto.feedback.KeeperCommentDto;
import org.example.dto.progress.CurrentCourseProgressDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.example.repository.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExplorerPublicInformationService {
    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;

    private final HomeworkService homeworkService;
    private final CourseService courseService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final FeedbackService feedbackService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerPublicInformation(Integer personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", ratingService.getPersonRatingAsExplorer(personId));
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(personId);
        List<KeeperCommentDto> feedback = feedbackService.getFeedbackForPersonAsExplorer(personExplorers);
        response.put("totalFeedback", feedback.size());
        response.put("totalSystems", personExplorers.size());
        Optional<CurrentCourseProgressDto> currentCourseOptional = courseProgressService
                .getCurrentCourseProgress(personId);
        if (currentCourseOptional.isEmpty()) {
            courseRegistrationRequestService
                    .getStudyRequestByExplorerPersonId(authenticatedPersonId, personId)
                    .ifPresent(
                            r -> response.put("studyRequest", r));
        } else {
            if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.KEEPER)) {
                homeworkService.getHomeworkRequestForKeeperFromPerson(authenticatedPersonId, personExplorers).ifPresent(
                        hr -> response.put("reviewRequest", hr)
                );
            }
            response.put("currentSystem", currentCourseOptional.get());
        }
        response.put("investigatedSystems", courseService.getCoursesRating(
                courseProgressService.getInvestigatedSystemIds(personExplorers)
        ));
        response.put("feedback", feedback);
        return response;
    }
}
