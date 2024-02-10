package org.example.galaxy.service.implementations;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.mapper.PersonWithSystemsMapper;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.service.ExplorerService;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExplorerServiceImpl implements ExplorerService {
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public List<PersonWithSystemsDto> getExplorersWithSystems(List<StarSystem> systems) {
        return PersonWithSystemsMapper.mapToExplorersWithSystems(
                explorerServiceBlockingStub.findExplorersPeopleByGroupCourseIdIn(
                        ExplorersService.ExplorersByGroup_CourseIdInRequest
                                .newBuilder()
                                .addAllCourseIds(systems.stream()
                                        .map(StarSystem::getSystemId)
                                        .collect(Collectors.toList())
                                ).build()
                ).getExplorersWithCourseIdMapMap()
        );
    }
}
