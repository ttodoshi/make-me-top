package org.example.person.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.example.person.mapper.KeeperMapper;
import org.example.person.model.Keeper;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.implementations.KeeperService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
                        keeperService.findKeepersByCourseId(
                                        "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                                        request.getCourseId()
                                )
                                .stream()
                                .map(KeeperMapper::mapKeeperToGrpcModel)
                                .collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void findKeepersPeopleByCourseIdIn(KeepersService.KeepersByCourseIdInRequest request, StreamObserver<KeepersService.KeepersPeopleByCourseIdInResponse> responseObserver) {
        List<Keeper> keepers = keeperService.findKeepersByCourseIdIn(
                request.getCourseIdsList()
        );
        Map<Long, Double> peopleRating = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
                keepers.stream()
                        .map(Keeper::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );

        Map<Long, List<PeopleService.PersonWithRating>> keepersWithRating = keepers
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

        responseObserver.onNext(KeepersService.KeepersPeopleByCourseIdInResponse
                .newBuilder().putAllKeepersWithCourseIdMap(
                        keepersWithRating.entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> KeepersService.KeepersPeopleByCourseIdInResponse.KeeperList
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
                                        e -> KeeperMapper.mapKeeperToGrpcModel(e.getValue())
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
                                                        .map(KeeperMapper::mapKeeperToGrpcModel)
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
                                        KeeperMapper::mapKeeperToGrpcModel)
                                ))
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void existsKeeperByPersonIdAndCourseId(KeepersService.KeeperByPersonIdAndCourseIdRequest request, StreamObserver<KeepersService.KeeperExistsResponse> responseObserver) {
        responseObserver.onNext(KeepersService.KeeperExistsResponse
                .newBuilder()
                .setKeeperExists(
                        keeperService.keeperExistsByPersonIdAndCourseId(
                                request.getPersonId(),
                                request.getCourseId()
                        )
                ).build()
        );
        responseObserver.onCompleted();
    }
}
