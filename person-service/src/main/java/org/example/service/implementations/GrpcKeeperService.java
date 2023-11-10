package org.example.service.implementations;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeeperServiceOuterClass;
import org.example.grpc.PeopleService;
import org.example.model.Keeper;
import org.example.repository.KeeperRepository;
import org.example.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcKeeperService extends KeeperServiceGrpc.KeeperServiceImplBase {
    private final KeeperRepository keeperRepository;
    private final RatingService ratingService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findKeepersByKeeperIdIn(KeeperServiceOuterClass.KeepersByKeeperIdInRequest request, StreamObserver<KeeperServiceOuterClass.KeepersByKeeperIdInResponse> responseObserver) {
        responseObserver.onNext(
                KeeperServiceOuterClass.KeepersByKeeperIdInResponse.newBuilder()
                        .addAllKeepers(keeperRepository
                                .findKeepersByKeeperIdIn(request.getKeeperIdsList())
                                .stream()
                                .map(k -> KeeperServiceOuterClass.Keeper.newBuilder()
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
    public void findAllKeepers(Empty request, StreamObserver<KeeperServiceOuterClass.AllKeepersResponse> responseObserver) {
        List<Keeper> keepers = keeperRepository.findAll();
        Map<Integer, Double> peopleRating = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
                keepers.stream()
                        .map(Keeper::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );
        Map<Integer, List<PeopleService.PersonWithRating>> collect = keepers
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
        responseObserver.onNext(KeeperServiceOuterClass.AllKeepersResponse
                .newBuilder().putAllKeepersWithCourseIdMap(
                        collect.entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> KeeperServiceOuterClass.AllKeepersResponse.KeeperList
                                                .newBuilder()
                                                .addAllPerson(entry.getValue())
                                                .addAllPerson(entry.getValue())
                                                .build()
                                ))).build()
        );
        responseObserver.onCompleted();
    }
}
