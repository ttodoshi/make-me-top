package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.RoleService;
import org.example.dto.courseregistration.CourseRegistrationRequestDTO;
import org.example.dto.feedback.KeeperFeedbackDTO;
import org.example.dto.feedback.PersonWithRating;
import org.example.dto.homework.HomeworkRequestDTO;
import org.example.dto.keeper.KeeperDTO;
import org.example.dto.courseprogress.CurrentCourseProgressDTO;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.model.course.Course;
import org.example.model.course.CourseTheme;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;

    private final RoleService roleService;

    @Transactional
    public Map<String, Object> getInformation() {
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            return getExplorerInformation();
        } else {
            return getKeeperInformation();
        }
    }

    private Map<String, Object> getKeeperInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", getKeeperRating(authenticatedPersonId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(authenticatedPersonId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(authenticatedPersonId));
        response.put("studyingExplorers", explorerRepository.getStudyingPeopleByKeeperPersonId(authenticatedPersonId));
        response.put("studyRequests", courseRegistrationRequestRepository.getStudyRequestsByKeeperPersonId(authenticatedPersonId));
        response.put("finalAssessments", explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(authenticatedPersonId));
        response.put("reviewRequests", homeworkRequestRepository.getReviewRequestsByKeeperPersonId(authenticatedPersonId));
        return response;
    }

    public Double getKeeperRating(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
        return Math.ceil(explorerFeedbackRepository.getKeeperRating(personId).orElse(0.0) * 10) / 10;
    }

    private Map<String, Object> getExplorerInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", getExplorerRating(authenticatedPersonId));
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(authenticatedPersonId));
        getCurrentCourseProgress(authenticatedPersonId)
                .ifPresent(p -> response.put("currentSystem", p));
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(authenticatedPersonId));
        response.put("ratingTable", getRatingTable());
        return response;
    }

    private Optional<CurrentCourseProgressDTO> getCurrentCourseProgress(Integer personId) {
        Optional<CurrentCourseProgressDTO> currentCourseProgressOptional = Optional.empty();
        Optional<Integer> currentSystemIdOptional = courseThemeCompletionRepository.getCurrentInvestigatedCourseId(personId);
        if (currentSystemIdOptional.isEmpty())
            return currentCourseProgressOptional;
        Integer currentSystemId = currentSystemIdOptional.get();
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, currentSystemId);
        if (explorerOptional.isEmpty())
            return currentCourseProgressOptional;
        Explorer explorer = explorerOptional.get();
        Double progress = courseThemeCompletionRepository.getCourseProgress(explorer.getExplorerId(), currentSystemId);
        Integer currentThemeId = getCurrentCourseThemeId(explorer);
        CourseTheme currentTheme = courseThemeRepository.findById(currentThemeId).orElseThrow(() -> new CourseThemeNotFoundException(currentThemeId));
        Course currentCourse = courseRepository.getReferenceById(currentSystemId);
        KeeperDTO keeper = keeperRepository.getKeeperForPersonOnCourse(personId, currentSystemId);
        return Optional.of(new CurrentCourseProgressDTO(explorer.getExplorerId(), currentTheme.getCourseThemeId(), currentTheme.getTitle(), currentCourse.getCourseId(), currentCourse.getTitle(), keeper, progress));
    }

    private Integer getCurrentCourseThemeId(Explorer explorer) {
        List<CourseThemeCompletionDTO> planetsProgress = getThemesProgress(explorer).getThemesWithProgress();
        for (CourseThemeCompletionDTO planet : planetsProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return planetsProgress.get(planetsProgress.size() - 1).getCourseThemeId();
    }

    private CourseWithThemesProgress getThemesProgress(Explorer explorer) {
        Course course = courseRepository.getReferenceById(explorer.getCourseId());
        List<CourseThemeCompletionDTO> planetsCompletion = new LinkedList<>();
        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(explorer.getCourseId())) {
            Boolean planetCompleted = courseThemeCompletionRepository.findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
            planetsCompletion.add(
                    new CourseThemeCompletionDTO(ct.getCourseThemeId(), ct.getTitle(), planetCompleted)
            );
        }
        return CourseWithThemesProgress.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .themesWithProgress(planetsCompletion)
                .build();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public Double getExplorerRating(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
        return Math.ceil(keeperFeedbackRepository.getExplorerRating(personId).orElse(0.0) * 10) / 10;
    }

    private List<PersonWithRating> getRatingTable() {
        return keeperFeedbackRepository.getRatingTable();
    }

    @Transactional
    public Map<String, Object> getKeeperPublicInformation(Integer personId) {
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", getKeeperRating(personId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(personId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(personId));
        response.put("systems", courseRepository.findCoursesByKeeperPersonId(personId));
        response.put("feedback", explorerFeedbackRepository.getKeeperCommentsByPersonId(personId));
        return response;
    }

    @Transactional
    public Map<String, Object> getExplorerPublicInformation(Integer personId) {
        Integer authenticatedPersonId = getAuthenticatedPersonId();
        Person person = personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", getExplorerRating(personId));
        List<KeeperFeedbackDTO> feedback = keeperFeedbackRepository.getExplorerCommentsByPersonId(personId);
        response.put("totalFeedback", feedback.size());
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(personId));
        Optional<CurrentCourseProgressDTO> currentCourseOptional = getCurrentCourseProgress(personId);
        if (currentCourseOptional.isEmpty()) {
            Optional<CourseRegistrationRequestDTO> studyRequestOptional = courseRegistrationRequestRepository.getStudyRequestByPersonId(personId);
            if (studyRequestOptional.isPresent() && keeperRepository.getReferenceById(studyRequestOptional.get().getKeeperId()).getPersonId().equals(authenticatedPersonId)) {
                response.put("studyRequest", studyRequestOptional.get());
            }
        } else {
            if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.KEEPER)) {
                List<HomeworkRequestDTO> homeworkRequests = homeworkRequestRepository
                        .getReviewRequestsByKeeperPersonId(authenticatedPersonId)
                        .stream()
                        .filter(h -> h.getPersonId().equals(personId))
                        .collect(Collectors.toList());
                if (!homeworkRequests.isEmpty()) {
                    Optional<HomeworkRequestDTO> homeworkRequestOptional = homeworkRequests
                            .stream()
                            .filter(hr -> hr.getPersonId().equals(personId))
                            .findFirst();
                    homeworkRequestOptional.ifPresent(homeworkRequestDTO -> response.put("reviewRequest", homeworkRequestDTO));
                }
            }
            response.put("currentSystem", currentCourseOptional.get());
        }
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(personId));
        response.put("feedback", feedback);
        return response;
    }
}
