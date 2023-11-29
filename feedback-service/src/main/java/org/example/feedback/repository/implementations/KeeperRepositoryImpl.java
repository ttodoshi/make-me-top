package org.example.feedback.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.feedback.repository.KeeperRepository;
import org.example.feedback.utils.AuthorizationHeaderContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    public Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    keeperServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findKeeperByPersonIdAndCourseId(
                                    KeepersService.KeeperByPersonIdAndCourseIdRequest.newBuilder()
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
    public List<KeepersService.Keeper> findKeepersByPersonId(Long personId) {
        return keeperServiceBlockingStub
                .findKeepersByPersonId(
                        KeepersService.KeepersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getKeepersList();
    }

    @Override
    public Map<Long, KeepersService.KeeperList> findKeepersByPersonIdIn(List<Long> personIds) {
        return keeperServiceBlockingStub
                .findKeepersByPersonIdIn(
                        KeepersService.KeepersByPersonIdInRequest.newBuilder()
                                .addAllPersonIds(personIds)
                                .build()
                ).getKeepersByPersonIdMapMap();
    }

    @Override
    public Optional<KeepersService.Keeper> findById(Long keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    keeperServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findKeeperById(
                                    KeepersService.KeeperByIdRequest.newBuilder()
                                            .setKeeperId(keeperId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
