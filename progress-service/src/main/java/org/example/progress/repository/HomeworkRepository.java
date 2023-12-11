package org.example.progress.repository;

import org.example.progress.dto.homework.HomeworkDto;

import java.util.List;
import java.util.Map;

public interface HomeworkRepository {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId);

    Map<Long, List<HomeworkDto>> findAllCompletedByCourseThemeIdAndGroupIdForExplorers(Long themeId, Long groupId, List<Long> explorerIds);
}
