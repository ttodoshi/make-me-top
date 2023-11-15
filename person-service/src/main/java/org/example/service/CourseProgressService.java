package org.example.service;

import org.example.dto.explorer.CurrentKeeperGroupDto;
import org.example.model.Explorer;
import org.example.model.ExplorerGroup;
import org.example.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.dto.progress.CurrentCourseProgressDto;

import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId);

    List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroup> keeperGroups);

    List<Integer> getInvestigatedSystemIds(List<Explorer> personExplorers);

    Optional<CurrentKeeperGroupDto> getCurrentGroup(List<ExplorerGroup> keeperGroups);
}
