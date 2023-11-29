package org.example.galaxy.service;

import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;
import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(Map<Long, KeepersService.AllKeepersResponse.KeeperList> keepers, List<StarSystem> systems);

    Map<Long, KeepersService.AllKeepersResponse.KeeperList> findKeepersWithCourseIds();
}
