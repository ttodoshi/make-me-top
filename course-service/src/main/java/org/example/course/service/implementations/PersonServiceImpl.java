package org.example.course.service.implementations;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.course.service.PersonService;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    @GrpcClient("people")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceBlockingStub;

    @Override
    public Map<Long, PeopleService.Person> findPeopleByPersonIdIn(String authorizationHeader, List<Long> personIds) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        return personServiceBlockingStub
                .withCallCredentials(callCredentials)
                .findPeopleByPersonIdIn(
                        PeopleService.PeopleByPersonIdInRequest.newBuilder()
                                .addAllPersonIds(personIds)
                                .build()
                ).getPeopleByPersonIdMapMap();
    }
}
