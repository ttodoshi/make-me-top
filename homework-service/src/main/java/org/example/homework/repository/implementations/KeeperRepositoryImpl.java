package org.example.homework.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.homework.repository.KeeperRepository;
import org.example.homework.utils.AuthorizationHeaderContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findKeepersByPersonIdAndCourseIdIn(
                        KeepersService.KeepersByPersonIdAndCourseIdInRequest
                                .newBuilder()
                                .setPersonId(personId)
                                .addAllCourseIds(courseIds)
                                .build()
                );
    }
}
