package org.example.feedback.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.feedback.exception.explorer.ExplorerNotFoundException;
import org.example.feedback.service.ExplorerService;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
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
    public List<ExplorersService.Explorer> findExplorersByPersonId(Long personId) {
        return explorerServiceBlockingStub
                .findExplorersByPersonId(
                        ExplorersService.ExplorersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getExplorersList();
    }

    @Override
    public Map<Long, ExplorersService.ExplorerList> findExplorersByGroup_CourseIdIn(String authorizationHeader, List<Long> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByGroupCourseIdIn(
                        ExplorersService.ExplorersByGroup_CourseIdInRequest.newBuilder()
                                .addAllCourseIds(courseIds)
                                .build()
                ).getExplorersByCourseIdMapMap();
    }

    @Override
    public Map<Long, ExplorersService.ExplorerList> findExplorersByPersonIdIn(List<Long> personIds) {
        return explorerServiceBlockingStub
                .findExplorersByPersonIdIn(
                        ExplorersService.ExplorersByPersonIdInRequest.newBuilder()
                                .addAllPersonIds(personIds)
                                .build()
                ).getExplorersByPersonIdMapMap();
    }
}
