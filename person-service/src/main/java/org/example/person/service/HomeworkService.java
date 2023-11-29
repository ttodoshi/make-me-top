package org.example.person.service;

import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.dto.homework.GetHomeworkRequestDto;

import java.util.List;
import java.util.Map;

public interface HomeworkService {
    List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Long, ExplorerGroup> explorerGroups);

    List<GetHomeworkRequestDto> getHomeworkRequestsFromPerson(List<Explorer> personExplorers);
}
