package org.example.service.implementations;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.KeeperServiceGrpc;
import org.example.grpc.KeepersService;
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
    public void findAllKeepers(Empty request, StreamObserver<KeepersService.AllKeepersResponse> responseObserver) {
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
        responseObserver.onNext(KeepersService.AllKeepersResponse
                .newBuilder().putAllKeepersWithCourseIdMap(
                        collect.entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> KeepersService.AllKeepersResponse.KeeperList
                                                .newBuilder()
                                                .addAllPerson(entry.getValue())
                                                .addAllPerson(entry.getValue())
                                                .build()
                                ))).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findKeepersByKeeperIdIn(KeepersService.KeepersByKeeperIdInRequest request, StreamObserver<KeepersService.KeepersByKeeperIdInResponse> responseObserver) {
        responseObserver.onNext(
                KeepersService.KeepersByKeeperIdInResponse.newBuilder()
                        .addAllKeepers(keeperRepository
                                .findKeepersByKeeperIdIn(request.getKeeperIdsList())
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
    public void findKeepersByPersonIdAndGroupCourseIdIn(KeepersService.KeepersByPersonIdAndGroup_CourseIdInRequest request, StreamObserver<KeepersService.KeepersByPersonIdAndGroup_CourseIdInResponse> responseObserver) {
        List<Integer> courseIdsList = request.getCourseIdsList();
        Map<Integer, KeepersService.Keeper> keepersByPersonIdWithCourseId = keeperRepository
                .findKeepersByPersonId(request.getPersonId())
                .stream()
                .filter(k -> courseIdsList.contains(k.getCourseId()))
                .collect(Collectors.toMap(
                        Keeper::getCourseId,
                        k -> KeepersService.Keeper
                                .newBuilder()
                                .setKeeperId(k.getKeeperId())
                                .setPersonId(k.getPersonId())
                                .setCourseId(k.getCourseId())
                                .setStartDate(
                                        Timestamp.newBuilder()
                                                .setSeconds(k.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                                .setNanos(k.getStartDate().getNano())
                                                .build()
                                ).build())
                );
        responseObserver.onNext(
                KeepersService.KeepersByPersonIdAndGroup_CourseIdInResponse
                        .newBuilder()
                        .putAllKeepersWithCourseIdMap(keepersByPersonIdWithCourseId)
                        .build()
        );
        responseObserver.onCompleted();
    }
}
