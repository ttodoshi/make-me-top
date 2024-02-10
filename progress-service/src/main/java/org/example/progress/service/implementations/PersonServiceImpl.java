package org.example.progress.service.implementations;

import io.grpc.CallCredentials;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.example.progress.exception.person.PersonNotFoundException;
import org.example.progress.service.PersonService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
            log.warn("person by id {} not found", personId);
            throw new PersonNotFoundException(personId);
        }
    }
}
