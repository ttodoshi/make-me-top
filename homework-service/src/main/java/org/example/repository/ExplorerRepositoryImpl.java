package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Optional<ExplorersService.Explorer> findById(Integer explorerId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    explorerServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findExplorerById(
                                    ExplorersService.ExplorerByIdRequest.newBuilder()
                                            .setExplorerId(explorerId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    explorerServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findExplorerByPersonIdAndGroupCourseId(
                                    ExplorersService.ExplorersByPersonIdAndGroupCourseIdRequest.newBuilder()
                                            .setPersonId(personId)
                                            .setCourseId(courseId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Map<Integer, ExplorersService.Explorer> findExplorersByExplorerIdIn(List<Integer> explorerIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByExplorerIdIn(
                        ExplorersService.ExplorersByExplorerIdInRequest.newBuilder()
                                .addAllExplorerIds(explorerIds)
                                .build()
                ).getExplorerByExplorerIdMapMap();
    }

    @Override
    public boolean existsById(Integer explorerId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .existsById(
                        ExplorersService.ExplorerByIdRequest.newBuilder()
                                .setExplorerId(explorerId)
                                .build()
                ).getExplorerExists();
    }
}
