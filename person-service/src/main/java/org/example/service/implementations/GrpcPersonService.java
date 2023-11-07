package org.example.service.implementations;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.example.model.Person;
import org.example.service.PersonService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZoneOffset;

@GrpcService
@RequiredArgsConstructor
public class GrpcPersonService extends PersonServiceGrpc.PersonServiceImplBase {
    private final PersonService personService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findPersonById(PeopleService.PersonByIdRequest request, StreamObserver<PeopleService.Person> responseObserver) {
        Person person = personService.findPersonById(request.getPersonId());
        responseObserver.onNext(PeopleService.Person.newBuilder()
                .setPersonId(person.getPersonId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setPatronymic(person.getPatronymic())
                .setRegistrationDate(Timestamp.newBuilder()
                        .setSeconds(person.getRegistrationDate().toEpochSecond(ZoneOffset.UTC))
                        .setNanos(person.getRegistrationDate().getNano())
                        .build())
                .setMaxExplorers(person.getMaxExplorers())
                .build()
        );
        responseObserver.onCompleted();
    }
}
