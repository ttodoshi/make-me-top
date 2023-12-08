package org.example.galaxy.service.implementations;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.service.KeeperService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperServiceImpl implements KeeperService {
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    @Cacheable(cacheNames = "keepersWithSystemsCache", key = "{#keepers, #systems}")
    public List<PersonWithSystemsDto> getKeepersWithSystems(
            Map<Long, KeepersService.AllKeepersResponse.KeeperList> keepers,
            List<StarSystem> systems) {
        return systems.stream()
                .flatMap(s ->
                        keepers.getOrDefault(s.getSystemId(), KeepersService.AllKeepersResponse.KeeperList.newBuilder().build())
                                .getPersonList()
                                .stream()
                                .map(k -> Map.entry(k, s.getSystemId())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))
                ).entrySet()
                .stream()
                .map(k -> new PersonWithSystemsDto(k.getKey().getPersonId(), k.getKey().getFirstName(), k.getKey().getLastName(), k.getKey().getPatronymic(), k.getKey().getRating(), k.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, KeepersService.AllKeepersResponse.KeeperList> findKeepersWithCourseIds() {
        return keeperServiceBlockingStub
                .findAllKeepers(Empty.newBuilder().build())
                .getKeepersWithCourseIdMapMap();
    }
}
