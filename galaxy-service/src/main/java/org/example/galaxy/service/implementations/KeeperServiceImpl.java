package org.example.galaxy.service.implementations;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.mapper.PersonWithSystemsMapper;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.service.KeeperService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeeperServiceImpl implements KeeperService {
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    public List<PersonWithSystemsDto> getKeepersWithSystems(List<StarSystem> systems) {
        return PersonWithSystemsMapper.mapToKeepersWithSystems(
                keeperServiceBlockingStub.findKeepersPeopleByCourseIdIn(
                        KeepersService.KeepersByCourseIdInRequest
                                .newBuilder()
                                .addAllCourseIds(systems.stream()
                                        .map(StarSystem::getSystemId)
                                        .collect(Collectors.toList())
                                ).build()
                ).getKeepersWithCourseIdMapMap()
        );
    }
}
