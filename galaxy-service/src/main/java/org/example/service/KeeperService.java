package org.example.service;

import org.example.dto.keeper.KeeperDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.model.StarSystem;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(Map<Integer, List<KeeperDto>> keepers, List<StarSystem> systems);

    Map<Integer, List<KeeperDto>> findKeepersWithCourseIds();
}
