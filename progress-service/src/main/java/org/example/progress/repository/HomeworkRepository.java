package org.example.progress.repository;

import org.example.progress.dto.homework.HomeworkDto;

import java.util.List;

public interface HomeworkRepository {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId);

    List<HomeworkDto> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(Integer courseThemeId,
                                                                           Integer groupId,
                                                                           Integer explorerId);
}
