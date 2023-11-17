package org.example.repository;

import io.grpc.CallCredentials;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @GrpcClient("people")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceBlockingStub;

    public Optional<PeopleService.Person> findById(Integer personId) {
        CallCredentials callCredentials = CallCredentialsHelper.authorizationHeader(
                authorizationHeaderRepository.getAuthorizationHeader()
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
