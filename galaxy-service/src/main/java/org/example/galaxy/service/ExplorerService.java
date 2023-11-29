package org.example.galaxy.service;

import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;
import org.example.grpc.ExplorersService;

import java.util.List;
import java.util.Map;

public interface ExplorerService {
    List<PersonWithSystemsDto> getExplorersWithSystems(Map<Long, ExplorersService.AllExplorersResponse.ExplorerList> explorers, List<StarSystem> systems);

    Map<Long, ExplorersService.AllExplorersResponse.ExplorerList> findExplorersWithCourseIds();
}
