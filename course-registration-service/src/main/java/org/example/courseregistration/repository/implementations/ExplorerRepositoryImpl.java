package org.example.courseregistration.repository.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.courseregistration.utils.AuthorizationHeaderContextHolder;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.courseregistration.repository.ExplorerRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId) {
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
    public void save(ExplorerCreateEvent explorer) {
        kafkaTemplate.send("explorerTopic", explorer);
    }

    @Override
    public ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse findExplorersByPersonIdAndGroupCourseIdIn(Long personId, List<Long> courseIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
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
}
