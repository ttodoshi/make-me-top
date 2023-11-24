package org.example.person.service;

import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.progress.CurrentCourseProgressDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId);

    List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroup> keeperGroups);

    List<Integer> getInvestigatedSystemIds(List<Explorer> personExplorers);

    Optional<CurrentKeeperGroupDto> getCurrentGroup();

    Optional<CurrentKeeperGroupDto> getCurrentGroup(List<ExplorerGroup> keeperGroups);
}
