package org.example.service;

import org.example.model.Explorer;
import org.example.model.ExplorerGroup;
import org.example.dto.homework.GetHomeworkRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HomeworkService {
    List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Integer, ExplorerGroup> explorerGroups);

    Optional<GetHomeworkRequestDto> getHomeworkRequestForKeeperFromPerson(Integer keeperPersonId, List<Explorer> personExplorers);
}
