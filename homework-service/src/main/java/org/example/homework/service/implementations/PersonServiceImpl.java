package org.example.homework.service.implementations;

import io.grpc.CallCredentials;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.example.homework.exception.person.PersonNotFoundException;
import org.example.homework.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PersonServiceImpl implements PersonService {
    @GrpcClient("people")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceBlockingStub;

    @Override
    public PeopleService.Person findPersonById(String authorizationHeader, Long personId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeader
        );
        try {
            return personServiceBlockingStub
                    .withCallCredentials(callCredentials)
                    .findPersonById(
                            PeopleService.PersonByIdRequest.newBuilder()
                                    .setPersonId(personId)
                                    .build()
                    );
        } catch (Exception e) {
            throw new PersonNotFoundException();
        }
    }

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
