package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerWithRatingDTO;
import org.example.dto.keeper.KeeperDTO;
import org.example.dto.systemprogress.CurrentCourseProgressDTO;
import org.example.model.Course;
import org.example.model.CourseTheme;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExplorerInformationService {
    private final ExplorerRepository explorerRepository;
    private final PlanetProgressRepository planetProgressRepository;
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final SystemProgressService systemProgressService;

    public Map<String, Object> getExplorerInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", getExplorerRating(authenticatedPersonId));
        response.put("totalSystems", explorerRepository.getExplorerSystemsCount(authenticatedPersonId));
        response.put("currentSystem", getCurrentSystemProgress(authenticatedPersonId));
        response.put("investigatedSystems", courseMarkRepository.getInvestigatedSystemsByPersonId(authenticatedPersonId));
        response.put("ratingTable", getRatingTable());
        return response;
    }

    private CurrentCourseProgressDTO getCurrentSystemProgress(Integer personId) {
        Integer currentSystemId = planetProgressRepository.getCurrentInvestigatedSystemId(personId);
        if (currentSystemId == null)
            return null;
        Optional<Explorer> explorerOptional = explorerRepository.findExplorerByPersonIdAndCourseId(personId, currentSystemId);
        Explorer explorer;
        if (explorerOptional.isEmpty())
            return null;
        else
            explorer = explorerOptional.get();
        Double progress = planetProgressRepository.getSystemProgress(explorer.getExplorerId(), currentSystemId);
        CourseTheme currentTheme = courseThemeRepository.findById(
                systemProgressService.getCurrentCourseThemeId(currentSystemId)).orElseThrow(); // ThemeNotFoundException
        Course currentCourse = courseRepository.getReferenceById(currentSystemId);
        KeeperDTO keeper = keeperRepository.getKeeperForPersonOnCourse(personId, currentSystemId);
        return new CurrentCourseProgressDTO(explorer.getExplorerId(), currentTheme.getCourseThemeId(), currentTheme.getTitle(), currentCourse.getCourseId(), currentCourse.getTitle(), keeper, progress);
    }

    // TODO
    private Double getExplorerRating(Integer personId) {
        return null;
    }

    // TODO
    private List<ExplorerWithRatingDTO> getRatingTable() {
        return Collections.emptyList();
    }
}
