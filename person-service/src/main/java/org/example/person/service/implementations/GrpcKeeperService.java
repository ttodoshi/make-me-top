package org.example.person.service.implementations;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.person.model.Keeper;
import org.example.person.service.KeeperService;
import org.example.person.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcKeeperService extends KeeperServiceGrpc.KeeperServiceImplBase {
    private final KeeperService keeperService;
    private final RatingService ratingService;

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findKeeperById(KeepersService.KeeperByIdRequest request, StreamObserver<KeepersService.Keeper> responseObserver) {
        Keeper keeper = keeperService.findKeeperByKeeperId(request.getKeeperId());
        responseObserver.onNext(
                KeepersService.Keeper.newBuilder()
                        .setKeeperId(keeper.getKeeperId())
                        .setCourseId(keeper.getCourseId())
                        .setPersonId(keeper.getPersonId())
                        .setStartDate(
                                Timestamp.newBuilder()
                                        .setSeconds(keeper.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                        .setNanos(keeper.getStartDate().getNano())
                                        .build()
                        ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findKeepersByPersonId(KeepersService.KeepersByPersonIdRequest request, StreamObserver<KeepersService.KeeperList> responseObserver) {
        responseObserver.onNext(KeepersService.KeeperList
                .newBuilder()
                .addAllKeepers(
                        keeperService
                                .findKeepersByPersonId(request.getPersonId())
                                .stream()
                                .map(k -> KeepersService.Keeper.newBuilder()
                                        .setKeeperId(k.getKeeperId())
                                        .setCourseId(k.getCourseId())
                                        .setPersonId(k.getPersonId())
                                        .setStartDate(
                                                Timestamp.newBuilder()
                                                        .setSeconds(k.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                                        .setNanos(k.getStartDate().getNano())
                                                        .build()
                                        ).build()
                                ).collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findKeeperByPersonIdAndCourseId(KeepersService.KeeperByPersonIdAndCourseIdRequest request, StreamObserver<KeepersService.Keeper> responseObserver) {
        Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                request.getPersonId(),
                request.getCourseId()
        );
        responseObserver.onNext(
                KeepersService.Keeper.newBuilder()
                        .setKeeperId(keeper.getKeeperId())
                        .setCourseId(keeper.getCourseId())
                        .setPersonId(keeper.getPersonId())
                        .setStartDate(
                                Timestamp.newBuilder()
                                        .setSeconds(keeper.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                        .setNanos(keeper.getStartDate().getNano())
                                        .build()
                        ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findKeepersByCourseId(KeepersService.KeepersByCourseIdRequest request, StreamObserver<KeepersService.KeeperList> responseObserver) {
        responseObserver.onNext(KeepersService.KeeperList
                .newBuilder()
                .addAllKeepers(
                        keeperService.findKeepersByCourseId(request.getCourseId())
                                .stream()
                                .map(this::mapKeeperToGrpcModel)
                                .collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findAllKeepers(Empty request, StreamObserver<KeepersService.AllKeepersResponse> responseObserver) {
        List<Keeper> keepers = keeperService.findAllKeepers();
        Map<Long, Double> peopleRating = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
                keepers.stream()
                        .map(Keeper::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );
        Map<Long, List<PeopleService.PersonWithRating>> collect = keepers
                .stream()
                .collect(Collectors.groupingBy(
                        Keeper::getCourseId,
                        Collectors.mapping(k -> PeopleService.PersonWithRating
                                        .newBuilder()
                                        .setPersonId(k.getPersonId())
                                        .setFirstName(k.getPerson().getFirstName())
                                        .setLastName(k.getPerson().getLastName())
                                        .setPatronymic(k.getPerson().getPatronymic())
                                        .setRating(peopleRating.get(k.getPersonId()))
                                        .build(),
                                Collectors.toList())
                ));
        responseObserver.onNext(KeepersService.AllKeepersResponse
                .newBuilder().putAllKeepersWithCourseIdMap(
                        collect.entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> KeepersService.AllKeepersResponse.KeeperList
                                                .newBuilder()
                                                .addAllPerson(entry.getValue())
                                                .build()
                                ))).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findKeepersByKeeperIdIn(KeepersService.KeepersByKeeperIdInRequest request, StreamObserver<KeepersService.KeepersByKeeperIdInResponse> responseObserver) {
        responseObserver.onNext(KeepersService.KeepersByKeeperIdInResponse
                .newBuilder()
                .putAllKeeperByKeeperIdMap(
                        keeperService.findKeepersByKeeperIdIn(
                                        request.getKeeperIdsList()
                                ).entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> this.mapKeeperToGrpcModel(e.getValue())
                                ))
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findKeepersByPersonIdIn(KeepersService.KeepersByPersonIdInRequest request, StreamObserver<KeepersService.KeepersByPersonIdInResponse> responseObserver) {
        responseObserver.onNext(KeepersService.KeepersByPersonIdInResponse
                .newBuilder()
                .putAllKeepersByPersonIdMap(request
                        .getPersonIdsList()
                        .stream()
                        .collect(Collectors.toMap(
                                pId -> pId,
                                pId -> KeepersService.KeeperList
                                        .newBuilder()
                                        .addAllKeepers(
                                                keeperService.findKeepersByPersonId(pId)
                                                        .stream()
                                                        .map(this::mapKeeperToGrpcModel)
                                                        .collect(Collectors.toList()))
                                        .build()
                        ))
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findKeepersByPersonIdAndCourseIdIn(KeepersService.KeepersByPersonIdAndCourseIdInRequest request, StreamObserver<KeepersService.KeepersByPersonIdAndCourseIdInResponse> responseObserver) {
        responseObserver.onNext(KeepersService.KeepersByPersonIdAndCourseIdInResponse
                .newBuilder()
                .putAllKeeperWithCourseIdMap(
                        keeperService.findKeepersByPersonIdAndCourseIdIn(
                                        request.getPersonId(),
                                        request.getCourseIdsList()
                                ).stream()
                                .collect(Collectors.toMap(
                                        Keeper::getCourseId,
                                        this::mapKeeperToGrpcModel)
                                ))
                .build()
        );
        responseObserver.onCompleted();
    }

    private KeepersService.Keeper mapKeeperToGrpcModel(Keeper keeper) {
        return KeepersService.Keeper
                .newBuilder()
                .setKeeperId(keeper.getKeeperId())
                .setPersonId(keeper.getPersonId())
                .setCourseId(keeper.getCourseId())
                .setStartDate(
                        Timestamp.newBuilder()
                                .setSeconds(keeper.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(keeper.getStartDate().getNano())
                                .build()
                ).build();
    }
}
