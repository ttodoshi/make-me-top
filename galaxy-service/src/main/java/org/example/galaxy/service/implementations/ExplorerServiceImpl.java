package org.example.galaxy.service.implementations;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.model.StarSystem;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.galaxy.service.ExplorerService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerServiceImpl implements ExplorerService {
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    @Cacheable(cacheNames = "explorersWithSystemsCache", key = "{#explorers, #systems}")
    public List<PersonWithSystemsDto> getExplorersWithSystems(
            Map<Integer, ExplorersService.AllExplorersResponse.ExplorerList> explorers,
            List<StarSystem> systems) {
        return systems.stream()
                .flatMap(s ->
                        explorers.getOrDefault(s.getSystemId(), ExplorersService.AllExplorersResponse.ExplorerList.newBuilder().build())
                                .getPersonList()
                                .stream()
                                .map(e -> Map.entry(e, s.getSystemId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(e -> new PersonWithSystemsDto(e.getKey().getPersonId(), e.getKey().getFirstName(), e.getKey().getLastName(), e.getKey().getPatronymic(), e.getKey().getRating(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, ExplorersService.AllExplorersResponse.ExplorerList> findExplorersWithCourseIds() {
        return explorerServiceBlockingStub
                .findAllExplorers(Empty.newBuilder().build())
                .getExplorersWithCourseIdMapMap();
    }
}
