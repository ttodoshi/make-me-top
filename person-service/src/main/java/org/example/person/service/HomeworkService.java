package org.example.person.service;

import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.dto.homework.GetHomeworkRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HomeworkService {
    List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Integer, ExplorerGroup> explorerGroups);

    Optional<GetHomeworkRequestDto> getHomeworkRequestForKeeperFromPerson(Integer keeperPersonId, List<Explorer> personExplorers);
}
