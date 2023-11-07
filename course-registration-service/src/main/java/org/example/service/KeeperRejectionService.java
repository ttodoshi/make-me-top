package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.dto.courserequest.KeeperRejectionDto;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestKeeper;
import org.example.model.KeeperRejection;
import org.example.model.RejectionReason;
import org.example.repository.*;
import org.example.service.validator.KeeperRejectionValidatorService;
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
    public KeeperRejection sendRejection(Integer requestId, KeeperRejectionDto rejection) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        PersonDto authenticatedPerson = personService.getAuthenticatedPerson();
        KeeperDto keeper = keeperRepository
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
