package org.example.service;

import org.example.dto.person.PersonWithSystemsDto;
import org.example.grpc.KeeperServiceOuterClass;
import org.example.model.StarSystem;

import java.util.List;
import java.util.Map;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(Map<Integer, KeeperServiceOuterClass.AllKeepersResponse.KeeperList> keepers, List<StarSystem> systems);

    Map<Integer, KeeperServiceOuterClass.AllKeepersResponse.KeeperList> findKeepersWithCourseIds();
}
