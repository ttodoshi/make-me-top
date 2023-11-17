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
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcPersonService extends PersonServiceGrpc.PersonServiceImplBase {
    private final PersonService personService;

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findPersonById(PeopleService.PersonByIdRequest request, StreamObserver<PeopleService.Person> responseObserver) {
        Person person = personService.findPersonById(request.getPersonId());
        responseObserver.onNext(
                mapPersonToGrpcModel(person)
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findPeopleByPersonIdIn(PeopleService.PeopleByPersonIdInRequest request, StreamObserver<PeopleService.PeopleByPersonIdInResponse> responseObserver) {
        Map<Integer, PeopleService.Person> personMap = personService.findPeopleByPersonIdIn(request.getPersonIdsList())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mapPersonToGrpcModel(e.getValue())
                ));
        responseObserver.onNext(PeopleService.PeopleByPersonIdInResponse
                .newBuilder()
                .putAllPeopleByPersonIdMap(personMap)
                .build()
        );
        responseObserver.onCompleted();
    }

    private PeopleService.Person mapPersonToGrpcModel(Person person) {
        return PeopleService.Person.newBuilder()
                .setPersonId(person.getPersonId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setPatronymic(person.getPatronymic())
                .setRegistrationDate(
                        Timestamp.newBuilder()
                                .setSeconds(person.getRegistrationDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(person.getRegistrationDate().getNano())
                                .build()
                )
                .setMaxExplorers(person.getMaxExplorers())
                .build();
    }
}
