package org.example.service;

import org.example.dto.explorer.ExplorerBasicInfoDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.dto.progress.CurrentCourseProgressDto;

import java.util.List;
import java.util.Optional;

public interface CourseProgressService {
    Optional<CurrentCourseProgressDto> getCurrentCourseProgress(Integer personId);

    List<ExplorerNeededFinalAssessmentDto> getExplorersNeededFinalAssessment(List<ExplorerGroupDto> keeperGroups);

    List<Integer> getInvestigatedSystemIds(List<ExplorerDto> personExplorers);

    List<ExplorerBasicInfoDto> getStudyingExplorersByKeeperPersonId(List<ExplorerGroupDto> keeperGroups);
}
