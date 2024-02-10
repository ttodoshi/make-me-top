package org.example.courseregistration.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.courseregistration.exception.keeper.KeeperNotFoundException;
import org.example.courseregistration.service.KeeperService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperServiceImpl implements KeeperService {
    @GrpcClient("keepers")
    private KeeperServiceGrpc.KeeperServiceBlockingStub keeperServiceBlockingStub;

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
            log.warn("keeper not found by person id {} and course id {}", personId, courseId);
            throw new KeeperNotFoundException();
        }
    }

    @Override
    public List<KeepersService.Keeper> findKeepersByPersonId(String authorizationHeader, Long personId) {
        return keeperServiceBlockingStub
                .findKeepersByPersonId(
                        KeepersService.KeepersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getKeepersList();
    }

    @Override
    public Map<Long, KeepersService.Keeper> findKeepersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return keeperServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findKeepersByKeeperIdIn(
                        KeepersService.KeepersByKeeperIdInRequest.newBuilder()
                                .addAllKeeperIds(keeperIds)
                                .build()
                ).getKeeperByKeeperIdMapMap();
    }

    @Override
    public Map<Long, KeepersService.Keeper> findKeepersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds) {
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
                ).getKeeperWithCourseIdMapMap();
    }
}
