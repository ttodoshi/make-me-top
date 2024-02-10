package org.example.person.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.ExplorerServiceGrpc;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.person.mapper.ExplorerMapper;
import org.example.person.model.Explorer;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.implementations.ExplorerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

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
        responseObserver.onNext(ExplorerMapper.mapExplorerToGrpcModel(explorer));
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
                                .map(ExplorerMapper::mapExplorerToGrpcModel)
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
                        explorerService.findExplorersByCourseId(
                                        "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                                        request.getCourseId()
                                )
                                .stream()
                                .map(ExplorerMapper::mapExplorerToGrpcModel)
                                .collect(Collectors.toList())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void findExplorerByPersonIdAndGroupCourseId(ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest request, StreamObserver<ExplorersService.Explorer> responseObserver) {
        Explorer explorer = explorerService.findExplorerByPersonIdAndCourseId(
                request.getPersonId(),
                request.getCourseId()
        );
        responseObserver.onNext(ExplorerMapper.mapExplorerToGrpcModel(explorer));
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void existsById(ExplorersService.ExplorerByIdRequest request, StreamObserver<ExplorersService.ExplorerExistsResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorerExistsResponse
                .newBuilder()
                .setExplorerExists(
                        explorerService.explorerExistsById(request.getExplorerId())
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public void existsExplorerByPersonIdAndGroupCourseId(ExplorersService.ExplorerByPersonIdAndGroupCourseIdRequest request, StreamObserver<ExplorersService.ExplorerExistsResponse> responseObserver) {
        responseObserver.onNext(ExplorersService.ExplorerExistsResponse
                .newBuilder()
                .setExplorerExists(
                        explorerService.existsExplorerByPersonIdAndGroup_CourseId(
                                request.getPersonId(),
                                request.getCourseId()
                        )
                ).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findExplorersPeopleByGroupCourseIdIn(ExplorersService.ExplorersByGroup_CourseIdInRequest request, StreamObserver<ExplorersService.ExplorersPeopleByGroup_CourseIdInResponse> responseObserver) {
        List<Explorer> explorers = explorerService.findExplorersByGroup_CourseIdIn(
                request.getCourseIdsList()
        );
        Map<Long, Double> peopleRating = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                explorers.stream()
                        .map(Explorer::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );

        Map<Long, List<PeopleService.PersonWithRating>> explorersByCourseId = explorers
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getGroup().getCourseId(),
                        Collectors.mapping(e -> PeopleService.PersonWithRating
                                .newBuilder()
                                .setPersonId(e.getPersonId())
                                .setFirstName(e.getPerson().getFirstName())
                                .setLastName(e.getPerson().getLastName())
                                .setPatronymic(e.getPerson().getPatronymic())
                                .setRating(peopleRating.get(e.getPersonId()))
                                .build(), Collectors.toList()))
                );

        responseObserver.onNext(ExplorersService.ExplorersPeopleByGroup_CourseIdInResponse
                .newBuilder()
                .putAllExplorersWithCourseIdMap(explorersByCourseId.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> ExplorersService.ExplorersPeopleByGroup_CourseIdInResponse.ExplorerList
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
                                .entrySet()
                                .stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> ExplorerMapper.mapExplorerToGrpcModel(e.getValue())
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
                                                        .map(ExplorerMapper::mapExplorerToGrpcModel)
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
        Map<Long, ExplorersService.ExplorerList> explorersByCourseId = explorerService.findExplorersByGroup_CourseIdIn(request.getCourseIdsList())
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getGroup().getCourseId(),
                        Collectors.mapping(
                                ExplorerMapper::mapExplorerToGrpcModel,
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
                                        ExplorerMapper::mapExplorerToGrpcModel
                                ))
                ).build()
        );
        responseObserver.onCompleted();
    }
}
