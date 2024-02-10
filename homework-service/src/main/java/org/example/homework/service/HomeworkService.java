package org.example.homework.service;

import org.example.homework.dto.homework.CreateHomeworkDto;
import org.example.homework.dto.homework.GetHomeworksWithRequestsDto;
import org.example.homework.dto.homework.HomeworkDto;
import org.example.homework.dto.homework.UpdateHomeworkDto;
import org.example.homework.dto.message.MessageDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface HomeworkService {
    HomeworkDto findHomeworkByHomeworkId(String authorizationHeader, Long authenticatedPersonId, Long homeworkId);

    List<HomeworkDto> findHomeworksByCourseThemeIdAndGroupId(String authorizationHeader, Authentication authentication, Long themeId, Long groupId);

    Map<Long, List<HomeworkDto>> findHomeworkByCourseThemeIdInAndGroupId(String authorizationHeader, Long authenticatedPersonId, List<Long> themeIds, Long groupId);

    Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> homeworkIds);

    Map<Long, Map<Long, List<HomeworkDto>>> findCompletedHomeworksByCourseThemeIdInAndGroupIdForExplorers(String authorizationHeader, Long authenticatedPersonId, List<Long> themeIds, Long groupId, List<Long> explorerIds);

    List<HomeworkDto> findHomeworksByThemeIdForExplorer(String authorizationHeader, Long authenticatedPersonId, Long themeId);

    GetHomeworksWithRequestsDto findHomeworksByThemeIdForKeeper(String authorizationHeader, Long authenticatedPersonId, Long themeId);

    Long addHomework(String authorizationHeader, Long authenticatedPersonId, Long themeId, CreateHomeworkDto homework);

    HomeworkDto updateHomework(String authorizationHeader, Long authenticatedPersonId, Long homeworkId, UpdateHomeworkDto homework);

    MessageDto deleteHomework(String authorizationHeader, Long authenticatedPersonId, Long homeworkId);
}
