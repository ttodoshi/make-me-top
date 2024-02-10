package org.example.person.service.api.progress;

import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.progress.CurrentCourseProgressProfileDto;
import org.example.person.dto.progress.CurrentCourseProgressPublicDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressProfileDto> getCurrentCourseProgressProfile(String authorizationHeader, Long personId);

    Optional<CurrentCourseProgressPublicDto> getCurrentCourseProgressPublic(String authorizationHeader, Long personId);

    List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(String authorizationHeader, List<ExplorerGroup> keeperGroups);

    List<Long> getInvestigatedSystemIds(String authorizationHeader, List<Explorer> personExplorers);

    Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader, Long authenticatedPersonId);

    Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader, List<ExplorerGroup> keeperGroups);
}
