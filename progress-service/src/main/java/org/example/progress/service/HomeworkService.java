package org.example.progress.service;

import org.example.progress.dto.homework.HomeworkDto;

import java.util.List;
import java.util.Map;

public interface HomeworkService {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(String authorizationHeader, Long themeId, Long groupId);

    Map<Long, List<HomeworkDto>> findHomeworksByCourseThemeIdInAndGroupId(String authorizationHeader, List<Long> themeIds, Long groupId);

    Map<Long, List<HomeworkDto>> findAllCompletedByCourseThemeIdAndGroupIdForExplorers(String authorizationHeader, Long themeId, Long groupId, List<Long> explorerIds);

    Map<Long, Map<Long, List<HomeworkDto>>> findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(String authorizationHeader, List<Long> collect, Long groupId, List<Long> collect1);
}
