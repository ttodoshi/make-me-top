package org.example.progress.repository;

import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.dto.planet.PlanetDto;

import java.util.List;
import java.util.Map;

public interface HomeworkRepository {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId);

    Map<Long, List<HomeworkDto>> findHomeworksByCourseThemeIdInAndGroupId(List<Long> themeIds, Long groupId);

    Map<Long, List<HomeworkDto>> findAllCompletedByCourseThemeIdAndGroupIdForExplorers(Long themeId, Long groupId, List<Long> explorerIds);

    Map<Long, Map<Long, List<HomeworkDto>>> findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(List<Long> collect, Long groupId, List<Long> collect1);
}
