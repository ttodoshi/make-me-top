package org.example.service.implementations;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.dto.explorer.ExplorerBasicInfoDto;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.repository.ExplorerRepository;
import org.example.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class GrpcExplorerService extends ExplorerServiceGrpc.ExplorerServiceImplBase {
    private final ExplorerRepository explorerRepository;

    private final RatingService ratingService;

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorersByGroupCourseIdIn(ExplorersService.ExplorersByGroup_CourseIdInRequest request,
                                               StreamObserver<ExplorersService.ExplorersByGroup_CourseIdInResponse> responseObserver) {
        Map<Integer, ExplorersService.ExplorersByGroup_CourseIdInResponse.ExplorerList> explorersByCourseId =
                request.getCourseIdsList().stream().collect(Collectors.toMap(
                        cId -> cId,
                        cId -> {
                            List<ExplorersService.Explorer> explorerList =
                                    explorerRepository.findExplorersByGroup_CourseId(cId)
                                            .stream()
                                            .map(e -> ExplorersService.Explorer.newBuilder()
                                                    .setExplorerId(e.getExplorerId())
                                                    .setPersonId(e.getPersonId())
                                                    .setGroupId(e.getGroupId())
                                                    .setStartDate(
                                                            Timestamp.newBuilder()
                                                                    .setSeconds(e.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                                                    .setNanos(e.getStartDate().getNano())
                                                                    .build()
                                                    ).build())
                                            .collect(Collectors.toList());
                            return ExplorersService.ExplorersByGroup_CourseIdInResponse.ExplorerList
                                    .newBuilder()
                                    .addAllExplorer(explorerList)
                                    .build();
                        }
                ));
        responseObserver.onNext(
                ExplorersService.ExplorersByGroup_CourseIdInResponse.newBuilder()
                        .putAllExplorersByCourseIdMap(explorersByCourseId)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void findAllExplorers(Empty request, StreamObserver<ExplorersService.AllExplorersResponse> responseObserver) {
        List<ExplorerBasicInfoDto> explorers = explorerRepository.findAll()
                .stream()
                .map(e -> new ExplorerBasicInfoDto(
                        e.getPersonId(),
                        e.getPerson().getFirstName(),
                        e.getPerson().getLastName(),
                        e.getPerson().getPatronymic(),
                        e.getExplorerId(),
                        e.getGroup().getCourseId(),
                        e.getGroupId()
                )).collect(Collectors.toList());
        Map<Integer, Double> peopleRating = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                explorers.stream()
                        .map(ExplorerBasicInfoDto::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );
        Map<Integer, List<PeopleService.PersonWithRating>> explorersByCourseId = explorers
                .stream()
                .collect(Collectors.groupingBy(
                        ExplorerBasicInfoDto::getCourseId,
                        Collectors.mapping(e -> PeopleService.PersonWithRating
                                .newBuilder()
                                .setPersonId(e.getPersonId())
                                .setFirstName(e.getFirstName())
                                .setLastName(e.getLastName())
                                .setPatronymic(e.getPatronymic())
                                .setRating(peopleRating.get(e.getPersonId()))
                                .build(), Collectors.toList()))
                );
        responseObserver.onNext(
                ExplorersService.AllExplorersResponse
                        .newBuilder()
                        .putAllExplorersWithCourseIdMap(explorersByCourseId.entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> ExplorersService.AllExplorersResponse.ExplorerList
                                                .newBuilder()
                                                .addAllPerson(entry.getValue())
                                                .build()
                                ))).build());
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void findExplorersByPersonIdAndGroupCourseIdIn(ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInRequest request, StreamObserver<ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse> responseObserver) {
        List<Integer> courseIdsList = request.getCourseIdsList();
        Map<Integer, ExplorersService.Explorer> explorersByPersonIdWithCourseId = explorerRepository
                .findExplorersByPersonId(request.getPersonId())
                .stream()
                .filter(e -> courseIdsList.contains(e.getGroup().getCourseId()))
                .collect(Collectors.toMap(
                        e -> e.getGroup().getCourseId(),
                        e -> ExplorersService.Explorer
                                .newBuilder()
                                .setExplorerId(e.getExplorerId())
                                .setPersonId(e.getPersonId())
                                .setGroupId(e.getGroupId())
                                .setStartDate(
                                        Timestamp.newBuilder()
                                                .setSeconds(e.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                                .setNanos(e.getStartDate().getNano())
                                                .build()
                                ).build())
                );
        responseObserver.onNext(
                ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse
                        .newBuilder()
                        .putAllExplorersWithCourseIdMap(explorersByPersonIdWithCourseId)
                        .build()
        );
        responseObserver.onCompleted();
    }
}
