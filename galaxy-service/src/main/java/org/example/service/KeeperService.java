package org.example.service;

import org.example.dto.person.PersonWithSystemsDto;
import org.example.grpc.KeepersService;
import org.example.model.StarSystem;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(Map<Integer, KeepersService.AllKeepersResponse.KeeperList> keepers, List<StarSystem> systems);

    Map<Integer, KeepersService.AllKeepersResponse.KeeperList> findKeepersWithCourseIds();
}
