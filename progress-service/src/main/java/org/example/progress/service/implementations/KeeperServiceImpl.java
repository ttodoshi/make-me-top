package org.example.progress.service.implementations;

import io.grpc.CallCredentials;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.progress.service.KeeperService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeeperServiceImpl implements KeeperService {
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

    @Override
    public KeepersService.Keeper findById(String authorizationHeader, Long keeperId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
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
    public List<KeepersService.Keeper> findKeepersByPersonId(Long personId) {
        return keeperServiceBlockingStub
                .findKeepersByPersonId(
                        KeepersService.KeepersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getKeepersList();
    }

    @Override
    public Boolean existsKeeperByPersonIdAndCourseId(String authorizationHeader, Long personId, Long courseId) {
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
