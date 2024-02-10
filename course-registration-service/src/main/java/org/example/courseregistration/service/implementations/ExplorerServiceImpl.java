package org.example.courseregistration.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.courseregistration.service.ExplorerService;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExplorerServiceImpl implements ExplorerService {
    private final KafkaTemplate<String, Object> createExplorerKafkaTemplate;
    @GrpcClient("explorers")
    private ExplorerServiceGrpc.ExplorerServiceBlockingStub explorerServiceBlockingStub;

    @Override
    public List<ExplorersService.Explorer> findExplorersByPersonId(String authorizationHeader, Long personId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return explorerServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findExplorersByPersonId(
                        ExplorersService.ExplorersByPersonIdRequest
                                .newBuilder()
                                .setPersonId(personId)
                                .build()
                ).getExplorersList();
    }

    @Override
    public Map<Long, ExplorersService.Explorer> findExplorersByPersonIdAndGroupCourseIdIn(String authorizationHeader, Long personId, List<Long> courseIds) {
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
                ).getExplorerWithCourseIdMapMap();
    }

    @Override
    public void save(ExplorerCreateEvent explorer) {
        createExplorerKafkaTemplate.send("createExplorerTopic", explorer);
    }
}
