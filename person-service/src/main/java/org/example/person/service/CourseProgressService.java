package org.example.person.service;

import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.progress.CurrentCourseProgressProfileDto;
import org.example.person.dto.progress.CurrentCourseProgressPublicDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressProfileDto> getCurrentCourseProgressProfile(Long personId);

    Optional<CurrentCourseProgressPublicDto> getCurrentCourseProgressPublic(Long personId);

    List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroup> keeperGroups);

    List<Long> getInvestigatedSystemIds(List<Explorer> personExplorers);

    Optional<CurrentKeeperGroupDto> getCurrentGroup();

    Optional<CurrentKeeperGroupDto> getCurrentGroup(List<ExplorerGroup> keeperGroups);
}
