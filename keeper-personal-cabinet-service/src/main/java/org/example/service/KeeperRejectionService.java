package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.KeeperRejectionDto;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestKeeper;
import org.example.model.courserequest.KeeperRejection;
import org.example.model.courserequest.RejectionReason;
import org.example.repository.KeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestKeeperRepository;
import org.example.repository.courserequest.CourseRegistrationRequestRepository;
import org.example.repository.courserequest.KeeperRejectionRepository;
import org.example.repository.courserequest.RejectionReasonRepository;
import org.example.service.validator.KeeperRejectionValidatorService;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final KeeperRejectionValidatorService keeperRejectionValidatorService;

    @Transactional
    public KeeperRejection sendRejection(Integer requestId, KeeperRejectionDto rejection) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(
                        authenticatedPerson.getPersonId(), request.getCourseId()
                ).orElseThrow(KeeperNotFoundException::new);
        CourseRegistrationRequestKeeper keeperResponse = courseRegistrationRequestKeeperRepository
                .findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(
                        requestId, keeper.getKeeperId()
                ).orElseThrow(DifferentKeeperException::new);
        keeperRejectionValidatorService.validateRejectionRequest(keeperResponse);
        return keeperRejectionRepository.save(
                new KeeperRejection(keeperResponse.getResponseId(), rejection.getReasonId())
        );
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasonRepository.findAll();
    }
}
