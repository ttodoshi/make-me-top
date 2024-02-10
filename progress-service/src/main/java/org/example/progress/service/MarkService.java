package org.example.progress.service;

import org.example.progress.dto.explorer.ExplorerBasicInfoDto;
import org.example.progress.dto.mark.CourseMarkDto;
import org.example.progress.dto.mark.MarkDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MarkService {
    CourseMarkDto findCourseMarkById(Long explorerId);

    Map<Long, Integer> findThemesMarks(String authorizationHeader, Long authenticatedPersonId, Long courseId);

    Long setCourseMark(String authorizationHeader, Long authenticatedPersonId, MarkDto mark);

    Set<Long> getThemesWaitingForExplorersMark(String authorizationHeader);

    List<ExplorerBasicInfoDto> getExplorersWaitingForThemeMark(String authorizationHeader, Long authenticatedPersonId, Long themeId);

    Long setThemeMark(String authorizationHeader, Long authenticatedPersonId, Long themeId, MarkDto mark);
}
