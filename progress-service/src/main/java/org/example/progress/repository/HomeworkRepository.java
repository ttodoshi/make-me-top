package org.example.progress.repository;

import org.example.progress.dto.homework.HomeworkDto;

import java.util.List;

public interface HomeworkRepository {
    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(Long themeId, Long groupId);

    List<HomeworkDto> findAllCompletedByCourseThemeIdAndGroupIdForExplorer(Long courseThemeId,
                                                                           Long groupId,
                                                                           Long explorerId);
}
