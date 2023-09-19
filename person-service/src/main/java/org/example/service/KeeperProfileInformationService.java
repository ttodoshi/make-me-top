package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.keeper.KeeperDto;
import org.example.model.Person;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperProfileInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperCabinetInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", ratingService.getPersonRatingAsKeeper(authenticatedPersonId));
        List<KeeperDto> keepers = keeperRepository.findKeepersByPersonId(authenticatedPersonId);
        response.put("totalSystems", keepers.size());
        List<ExplorerGroupDto> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(KeeperDto::getKeeperId).collect(Collectors.toList())
        );
        response.put("totalExplorers", explorerGroups.stream().mapToLong(g -> g.getExplorers().size()).sum());
        response.put("studyingExplorers", courseProgressService.getStudyingExplorersByKeeperPersonId(explorerGroups));
        response.put("studyRequests", courseRegistrationRequestService.getStudyRequestsForKeeper(keepers));
        response.put("finalAssessments", courseProgressService.getExplorersNeededFinalAssessment(explorerGroups));
        response.put("reviewRequests", homeworkService.getHomeworkRequestsFromExplorersByGroups(
                explorerGroups.stream().collect(Collectors.toMap(ExplorerGroupDto::getGroupId, g -> g))
        ));
        return response;
    }
}
