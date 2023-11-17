package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.dto.event.ExplorerCreateEvent;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerRepositoryImpl implements ExplorerRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public Optional<ExplorersService.Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer courseId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
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
}
