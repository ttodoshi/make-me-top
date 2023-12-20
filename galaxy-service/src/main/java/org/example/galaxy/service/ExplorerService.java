package org.example.galaxy.service;

import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;

import java.util.List;

public interface ExplorerService {
    List<PersonWithSystemsDto> getExplorersWithSystems(List<StarSystem> systems);
}
