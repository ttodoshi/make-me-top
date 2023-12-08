package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.dto.courserequest.KeeperRejectionDto;
import org.example.courseregistration.dto.courserequest.RejectionReasonDto;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.KeeperRejection;
import org.example.courseregistration.repository.*;
import org.example.courseregistration.service.validator.KeeperRejectionValidatorService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperRejectionService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestKeeperRepository courseRegistrationRequestKeeperRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final KeeperRejectionValidatorService keeperRejectionValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public KeeperRejectionDto sendRejection(Long requestId, CreateKeeperRejectionDto rejection) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        PeopleService.Person authenticatedPerson = personService.getAuthenticatedPerson();

        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        authenticatedPerson.getPersonId(), request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        requestId, keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);

        keeperRejectionValidatorService.validateRejectionRequest(rejection, keeperResponse);

        return mapper.map(
                keeperRejectionRepository.save(
                        new KeeperRejection(keeperResponse, rejection.getReasonId())
                ),
                KeeperRejectionDto.class
        );
    }

    @Transactional(readOnly = true)
    public List<RejectionReasonDto> getRejectionReasons() {
        return rejectionReasonRepository.findAll()
                .stream()
                .map(r -> mapper.map(r, RejectionReasonDto.class))
                .collect(Collectors.toList());
    }
}
