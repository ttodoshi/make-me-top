package org.example.progress.service;

import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.progress.dto.progress.CoursesStateDto;
import org.example.progress.dto.progress.ExplorerProgressDto;

import java.util.List;

public interface ProgressService {
    CoursesStateDto getCoursesProgressForCurrentUser(String authorizationHeader, Long authenticatedPersonId, Long galaxyId);

    CourseWithThemesProgressDto getExplorerThemesProgress(String authorizationHeader, Long explorerId);

    List<Long> getExplorerIdsNeededFinalAssessment(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds);

    List<Long> getExplorerIdsWithFinalAssessment(List<Long> explorerIds);

    ExplorerProgressDto getExplorerCourseProgress(String authorizationHeader, Long authenticatedPersonId, Long courseId);
}
