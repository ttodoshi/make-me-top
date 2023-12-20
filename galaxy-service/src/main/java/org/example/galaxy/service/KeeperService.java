package org.example.galaxy.service;

import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;

import java.util.List;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(List<StarSystem> systems);
}
