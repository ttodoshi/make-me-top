package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    public Optional<KeepersService.Keeper> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
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
                authorizationHeaderRepository.getAuthorizationHeader()
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findKeeperById(
                        KeepersService.KeeperByIdRequest.newBuilder()
                                .setKeeperId(keeperId)
                                .build()
                );
    }
}
