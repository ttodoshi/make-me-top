package org.example.service;

import org.example.dto.person.PersonWithSystemsDto;
import org.example.grpc.ExplorersService;
import org.example.model.StarSystem;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    List<PersonWithSystemsDto> getExplorersWithSystems(Map<Integer, ExplorersService.AllExplorersResponse.ExplorerList> explorers, List<StarSystem> systems);

    Map<Integer, ExplorersService.AllExplorersResponse.ExplorerList> findExplorersWithCourseIds();
}
