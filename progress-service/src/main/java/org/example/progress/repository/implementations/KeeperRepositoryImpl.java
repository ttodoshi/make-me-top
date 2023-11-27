package org.example.progress.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.progress.repository.KeeperRepository;
import org.example.progress.utils.AuthorizationHeaderContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    public Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
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
    public KeepersService.Keeper getReferenceById(Integer keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findKeeperById(
                        KeepersService.KeeperByIdRequest.newBuilder()
                                .setKeeperId(keeperId)
                                .build()
                );
    }

    @Override
    public List<KeepersService.Keeper> findKeepersByPersonId(Integer personId) {
        return keeperServiceBlockingStub
                .findKeepersByPersonId(
                        KeepersService.KeepersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getKeepersList();
    }
}
