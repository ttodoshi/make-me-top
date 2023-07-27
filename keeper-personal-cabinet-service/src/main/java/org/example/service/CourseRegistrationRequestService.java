package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CourseRegistrationRequestReply;
import org.example.dto.courseregistration.KeeperRejectionDTO;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.requestEX.*;
import org.example.model.Explorer;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.courserequest.*;
import org.example.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    @Transactional
    public CourseRegistrationRequest replyToRequest(Integer requestId, CourseRegistrationRequestReply requestReply) {
        final Integer authenticatedPersonId = getAuthenticatedPersonId();
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.PROCESSING))
            throw new RequestAlreadyClosedException(requestId);
        if (authenticatedKeeperIsNotKeeperInRequest(authenticatedPersonId, request))
            throw new DifferentKeeperException();
        CourseRegistrationRequestStatusType status;
        if (requestReply.getApproved()) {
            status = CourseRegistrationRequestStatusType.APPROVED;
            addExplorer(request.getPersonId(), request.getCourseId());
        } else {
            status = CourseRegistrationRequestStatusType.DENIED;
        }
        Integer statusId = courseRegistrationRequestStatusRepository
                .findCourseRegistrationRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status)).getStatusId();
        request.setStatusId(statusId);
        return courseRegistrationRequestRepository.save(request);
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    private void addExplorer(Integer personId, Integer courseId) {
        explorerRepository.save(
                Explorer.builder()
                        .personId(personId)
                        .courseId(courseId)
                        .build()
        );
    }

    @Transactional
    public KeeperRejection sendRejection(Integer requestId, KeeperRejectionDTO rejection) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        CourseRegistrationRequestStatus currentStatus = courseRegistrationRequestStatusRepository.getReferenceById(request.getStatusId());
        if (!currentStatus.getStatus().equals(CourseRegistrationRequestStatusType.DENIED))
            throw new RequestNotDeniedException(requestId);
        if (keeperRejectionRepository.findKeeperRejectionByRequestId(requestId).isPresent())
            throw new KeeperRejectionAlreadyExistsException();
        if (authenticatedKeeperIsNotKeeperInRequest(authenticatedPerson.getPersonId(), request))
            throw new DifferentKeeperException();
        return keeperRejectionRepository.save(
                KeeperRejection.builder()
                        .requestId(requestId)
                        .reasonId(rejection.getReasonId())
                        .build()
        );
    }

    private boolean authenticatedKeeperIsNotKeeperInRequest(Integer personId, CourseRegistrationRequest request) {
        Optional<Keeper> keeperOptional = keeperRepository.findKeeperByPersonIdAndCourseId(personId, request.getCourseId());
        return (keeperOptional.isEmpty() || !keeperOptional.get().getKeeperId().equals(request.getKeeperId()));
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasonRepository.findAll();
    }
}