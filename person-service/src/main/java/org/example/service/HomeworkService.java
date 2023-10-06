package org.example.service;

import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.homework.GetHomeworkRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HomeworkService {
    List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(Map<Integer, ExplorerGroupDto> explorerGroups);

    Optional<GetHomeworkRequestDto> getHomeworkRequestForKeeperFromPerson(Integer keeperPersonId, List<ExplorerDto> personExplorers);
}
