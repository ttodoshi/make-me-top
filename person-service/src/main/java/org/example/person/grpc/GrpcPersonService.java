package org.example.person.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.PeopleService;
import org.example.grpc.PersonServiceGrpc;
import org.example.person.mapper.PersonMapper;
import org.example.person.model.Person;
import org.example.person.service.implementations.PersonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

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
                PersonMapper.mapPersonToGrpcModel(person)
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findPeopleByPersonIdIn(PeopleService.PeopleByPersonIdInRequest request, StreamObserver<PeopleService.PeopleByPersonIdInResponse> responseObserver) {
        Map<Long, PeopleService.Person> personMap = personService.findPeopleByPersonIdIn(request.getPersonIdsList())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> PersonMapper.mapPersonToGrpcModel(e.getValue())
                ));
        responseObserver.onNext(PeopleService.PeopleByPersonIdInResponse
                .newBuilder()
                .putAllPeopleByPersonIdMap(personMap)
                .build()
        );
        responseObserver.onCompleted();
    }
}
