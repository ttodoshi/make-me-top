package org.example.service;

import org.example.dto.explorer.ExplorerDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.model.StarSystem;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    List<PersonWithSystemsDto> getExplorersWithSystems(Map<Integer, List<ExplorerDto>> explorers, List<StarSystem> systems);

    Map<Integer, List<ExplorerDto>> findExplorersWithCourseIds();
}
