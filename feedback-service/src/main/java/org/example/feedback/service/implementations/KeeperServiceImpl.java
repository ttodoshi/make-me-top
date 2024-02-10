package org.example.feedback.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.feedback.exception.keeper.KeeperNotFoundException;
import org.example.feedback.service.KeeperService;
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
            log.warn("keeper by id {} not found", keeperId);
            throw new KeeperNotFoundException(keeperId);
        }
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
            log.warn("keeper by person id {} and course id {} not found", personId, courseId);
            throw new KeeperNotFoundException();
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
}
