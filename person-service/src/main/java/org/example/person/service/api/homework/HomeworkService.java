package org.example.person.service.api.homework;

import org.example.person.dto.homework.GetHomeworkRequestDto;
import org.example.person.dto.homework.GetHomeworkWithMarkDto;
import org.example.person.dto.homework.HomeworkDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;

import java.util.List;
import java.util.Map;

public interface HomeworkService {
    Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(String authorizationHeader, List<Long> homeworkIds);

    List<GetHomeworkWithMarkDto> findHomeworksByCourseThemeId(String authorizationHeader, Long themeId);

    List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(String authorizationHeader, Map<Long, ExplorerGroup> explorerGroups);

    List<GetHomeworkRequestDto> getHomeworkRequestsFromPerson(String authorizationHeader, List<Explorer> personExplorers);
}
