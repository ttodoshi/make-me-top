package org.example.service.implementations;

import com.google.protobuf.Empty;
import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeeperServiceOuterClass;
import org.example.model.StarSystem;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.KeeperService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperServiceImpl implements KeeperService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    @Cacheable(cacheNames = "keepersWithSystemsCache", key = "{#keepers, #systems}")
    public List<PersonWithSystemsDto> getKeepersWithSystems(
            Map<Integer, KeeperServiceOuterClass.AllKeepersResponse.KeeperList> keepers,
            List<StarSystem> systems) {
        return systems.stream()
                .flatMap(s ->
                        keepers.getOrDefault(s.getSystemId(), KeeperServiceOuterClass.AllKeepersResponse.KeeperList.newBuilder().build())
                                .getPersonList()
                                .stream()
                                .map(k -> Map.entry(k, s.getSystemId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(k -> new PersonWithSystemsDto(k.getKey().getPersonId(), k.getKey().getFirstName(), k.getKey().getLastName(), k.getKey().getPatronymic(), k.getKey().getRating(), k.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, KeeperServiceOuterClass.AllKeepersResponse.KeeperList> findKeepersWithCourseIds() {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return keeperServiceBlockingStub.withCallCredentials(callCredentials)
                .findAllKeepers(Empty.newBuilder().build())
                .getKeepersWithCourseIdMapMap();
    }
}
