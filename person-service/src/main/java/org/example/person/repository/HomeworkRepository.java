package org.example.person.repository;

import org.example.person.dto.homework.GetHomeworkWithMarkDto;
import org.example.person.dto.homework.HomeworkDto;

import java.util.List;
import java.util.Map;

public interface HomeworkRepository {
    Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(List<Long> homeworkIds);

    List<GetHomeworkWithMarkDto> findHomeworksByCourseThemeId(Long themeId);
}
