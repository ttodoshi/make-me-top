package org.example.auth.repository.implementations;

import io.grpc.CallCredentials;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.auth.utils.AuthorizationHeaderContextHolder;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.example.auth.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonRepositoryImpl implements PersonRepository {
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    @GrpcClient("people")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceBlockingStub;

    public PersonRepositoryImpl(@Qualifier("authorizationHeaderContextHolder") AuthorizationHeaderContextHolder authorizationHeaderContextHolder) {
        this.authorizationHeaderContextHolder = authorizationHeaderContextHolder;
    }

    public Optional<PeopleService.Person> findById(Integer personId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderContextHolder.getAuthorizationHeader()
        );
        try {
            return Optional.of(
                    personServiceBlockingStub
                            .withCallCredentials(callCredentials)
                            .findPersonById(
                                    PeopleService.PersonByIdRequest.newBuilder()
                                            .setPersonId(personId)
                                            .build()
                            )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
