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
import org.example.model.Explorer;
import org.example.service.ExplorerService;
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
    private final ExplorerService explorerService;
    private final RatingService ratingService;

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerById(ExplorersService.ExplorerByIdRequest request, StreamObserver<ExplorersService.Explorer> responseObserver) {
        Explorer explorer = explorerService.findExplorerById(request.getExplorerId());
        responseObserver.onNext(mapExplorerToGrpcModel(explorer));
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findExplorersByPersonId(ExplorersService.ExplorersByPersonIdRequest request, StreamObserver<ExplorersService.ExplorerList> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorerList
                .newBuilder()
                .addAllExplorers(
                        explorerService.findExplorersByPersonId(request.getPersonId())
                                .stream()
                                .map(this::mapExplorerToGrpcModel)
                                .collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorersByGroupCourseId(ExplorersService.ExplorersByGroup_CourseIdRequest request, StreamObserver<ExplorersService.ExplorerList> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorerList
                .newBuilder()
                .addAllExplorers(
                        explorerService.findExplorersByCourseId(request.getCourseId())
                                .stream()
                                .map(this::mapExplorerToGrpcModel)
                                .collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerByPersonIdAndGroupCourseId(ExplorersService.ExplorersByPersonIdAndGroupCourseIdRequest request, StreamObserver<ExplorersService.Explorer> responseObserver) {
        Explorer explorer = explorerService.findExplorerByPersonIdAndCourseId(
                request.getPersonId(),
                request.getCourseId()
        );
        responseObserver.onNext(mapExplorerToGrpcModel(explorer));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void existsById(ExplorersService.ExplorerByIdRequest request, StreamObserver<ExplorersService.ExplorerExistsByIdResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorerExistsByIdResponse
                .newBuilder()
                .setExplorerExists(
                        explorerService.explorerExistsById(request.getExplorerId())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findAllExplorers(Empty request, StreamObserver<ExplorersService.AllExplorersResponse> responseObserver) {
        List<ExplorerBasicInfoDto> explorers = explorerService.findAllExplorers();
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
        responseObserver.onNext(ExplorersService.AllExplorersResponse
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
    @Transactional(readOnly = true)
    public void findExplorersByExplorerIdIn(ExplorersService.ExplorersByExplorerIdInRequest request, StreamObserver<ExplorersService.ExplorersByExplorerIdInResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorersByExplorerIdInResponse
                .newBuilder()
                .putAllExplorerByExplorerIdMap(
                        explorerService.findExplorersByExplorerIdIn(request.getExplorerIdsList())
                                .stream()
                                .collect(Collectors.toMap(
                                        Explorer::getExplorerId,
                                        this::mapExplorerToGrpcModel
                                ))
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findExplorersByPersonIdIn(ExplorersService.ExplorersByPersonIdInRequest request, StreamObserver<ExplorersService.ExplorersByPersonIdInResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorersByPersonIdInResponse
                .newBuilder()
                .putAllExplorersByPersonIdMap(request
                        .getPersonIdsList()
                        .stream()
                        .collect(Collectors.toMap(
                                pId -> pId,
                                pId -> ExplorersService.ExplorerList
                                        .newBuilder()
                                        .addAllExplorers(
                                                explorerService.findExplorersByPersonId(pId)
                                                        .stream()
                                                        .map(this::mapExplorerToGrpcModel)
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
    public void findExplorersByGroupCourseIdIn(ExplorersService.ExplorersByGroup_CourseIdInRequest request,
                                               StreamObserver<ExplorersService.ExplorersByGroup_CourseIdInResponse> responseObserver) {
        Map<Integer, ExplorersService.ExplorerList> explorersByCourseId = explorerService.findExplorersByGroup_CourseIdIn(request.getCourseIdsList())
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getGroup().getCourseId(),
                        Collectors.mapping(
                                this::mapExplorerToGrpcModel,
                                Collectors.toList()
                        )
                )).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> ExplorersService.ExplorerList
                                .newBuilder()
                                .addAllExplorers(e.getValue())
                                .build()
                ));
        responseObserver.onNext(
                ExplorersService.ExplorersByGroup_CourseIdInResponse.newBuilder()
                        .putAllExplorersByCourseIdMap(explorersByCourseId)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorersByPersonIdAndGroupCourseIdIn(ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInRequest request, StreamObserver<ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorersByPersonIdAndGroup_CourseIdInResponse
                .newBuilder()
                .putAllExplorerWithCourseIdMap(
                        explorerService.findExplorersByPersonIdAndGroup_CourseIdIn(
                                        request.getPersonId(),
                                        request.getCourseIdsList()
                                ).stream()
                                .collect(Collectors.toMap(
                                        e -> e.getGroup().getCourseId(),
                                        this::mapExplorerToGrpcModel
                                ))
                ).build()
        );
        responseObserver.onCompleted();
    }


    public ExplorersService.Explorer mapExplorerToGrpcModel(Explorer explorer) {
        return ExplorersService.Explorer
                .newBuilder()
                .setExplorerId(explorer.getExplorerId())
                .setPersonId(explorer.getPersonId())
                .setGroupId(explorer.getGroupId())
                .setStartDate(
                        Timestamp.newBuilder()
                                .setSeconds(explorer.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(explorer.getStartDate().getNano())
                                .build()
                ).build();
    }
}
