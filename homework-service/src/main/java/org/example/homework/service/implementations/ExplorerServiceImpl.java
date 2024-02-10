package org.example.homework.service.implementations;

import io.grpc.CallCredentials;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.homework.exception.explorer.ExplorerNotFoundException;
import org.example.homework.service.ExplorerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExplorerServiceImpl implements ExplorerService {
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public ExplorersService.Explorer findById(String authorizationHeader, Long explorerId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return explorerServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findExplorerById(
                            ExplorersService.ExplorerByIdRequest.newBuilder()
                                    .setExplorerId(explorerId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("explorer by id {} not found", explorerId);
            throw new ExplorerNotFoundException(explorerId);
        }
    }

    @Override
    public ExplorersService.Explorer findExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return explorerServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findExplorerByPersonIdAndGroupCourseId(
                            ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest.newBuilder()
                                    .setPersonId(personId)
                                    .setCourseId(courseId)
                                    .build()
                    );
        } catch (Exception e) {
            log.warn("explorer by person id {} and course id {} not found", personId, courseId);
            throw new ExplorerNotFoundException();
        }
    }

    @Override
    public Map<Long, ExplorersService.Explorer> findExplorersByExplorerIdIn(String authorizationHeader, List<Long> explorerIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
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
    public ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByPersonIdAndGroupCourseIdIn(
                        ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInRequest
                                .newBuilder()
                                .setPersonId(personId)
                                .addAllCourseIds(courseIds)
                                .build()
                );
    }

    @Override
    public boolean existsExplorerByPersonIdAndGroup_CourseId(String authorizationHeader, Long personId, Long courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .existsExplorerByPersonIdAndGroupCourseId(
                        ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest.newBuilder()
                                .setPersonId(personId)
                                .setCourseId(courseId)
                                .build()
                ).getExplorerExists();
    }
}
