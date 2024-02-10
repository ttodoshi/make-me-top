package org.example.homework.service.implementations;

import io.grpc.CallCredentials;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.homework.exception.keeper.KeeperNotFoundException;
import org.example.homework.service.KeeperService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeeperServiceImpl implements KeeperService {
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    public KeepersService.Keeper findKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return keeperServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findKeeperByPersonIdAndCourseId(
                            KeepersService.KeeperByPersonIdAndCourseIdRequest.newBuilder()
                                    .setPersonId(personId)
                                    .setCourseId(courseId)
                                    .build()
                    );
        } catch (Exception e) {
            throw new KeeperNotFoundException();
        }
    }

    @Override
    public KeepersService.Keeper findById(String authorizationHeader, Long keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return keeperServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findKeeperById(
                            KeepersService.KeeperByIdRequest.newBuilder()
                                    .setKeeperId(keeperId)
                                    .build()
                    );
        } catch (Exception e) {
            throw new KeeperNotFoundException();
        }
    }

    @Override
    public KeepersService.KeepersByPersonIdAndCourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
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

    @Override
    public boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .existsKeeperByPersonIdAndCourseId(
                        KeepersService.KeeperByPersonIdAndCourseIdRequest.newBuilder()
                                .setPersonId(personId)
                                .setCourseId(courseId)
                                .build()
                ).getKeeperExists();
    }
}
