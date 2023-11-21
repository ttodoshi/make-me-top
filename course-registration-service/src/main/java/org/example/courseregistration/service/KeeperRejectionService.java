package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.exception.classes.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.classes.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.example.courseregistration.model.KeeperRejection;
import org.example.courseregistration.model.RejectionReason;
import org.example.courseregistration.repository.*;
import org.example.courseregistration.service.validator.KeeperRejectionValidatorService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public KeeperRejection sendRejection(Integer requestId, CreateKeeperRejectionDto rejection) {
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
        return keeperRejectionRepository.save(
                new KeeperRejection(keeperResponse, rejection.getReasonId())
        );
    }

    @Transactional(readOnly = true)
    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasonRepository.findAll();
    }
}
