package org.example.repository;

import org.example.dto.homework.HomeworkDto;

import java.util.List;

public interface HomeworkRepository {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Integer themeId, Integer groupId);

    List<HomeworkDto> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(Integer courseThemeId,
                                                                           Integer groupId,
                                                                           Integer explorerId);
}
