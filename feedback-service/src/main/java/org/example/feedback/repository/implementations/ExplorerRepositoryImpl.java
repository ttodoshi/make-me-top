package org.example.feedback.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.feedback.repository.ExplorerRepository;
import org.example.feedback.utils.AuthorizationHeaderContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Optional<ExplorersService.Explorer> findById(Integer explorerId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    explorerServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findExplorerById(
                                    ExplorersService.ExplorerByIdRequest.newBuilder()
                                            .setExplorerId(explorerId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    explorerServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findExplorerByPersonIdAndGroupCourseId(
                                    ExplorersService.ExplorersByPersonIdAndGroupCourseIdRequest.newBuilder()
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
    public List<ExplorersService.Explorer> findExplorersByPersonId(Integer personId) {
        return explorerServiceBlockingStub
                .findExplorersByPersonId(
                        ExplorersService.ExplorersByPersonIdRequest.newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getExplorersList();
    }

    @Override
    public Map<Integer, ExplorersService.ExplorerList> findExplorersByGroup_CourseIdIn(List<Integer> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
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
    public Map<Integer, ExplorersService.ExplorerList> findExplorersByPersonIdIn(List<Integer> personIds) {
        return explorerServiceBlockingStub
                .findExplorersByPersonIdIn(
                        ExplorersService.ExplorersByPersonIdInRequest.newBuilder()
                                .addAllPersonIds(personIds)
                                .build()
                ).getExplorersByPersonIdMapMap();
    }
}
