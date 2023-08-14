package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CourseRegistrationRequestReply;
import org.example.dto.courseregistration.KeeperRejectionDTO;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.Explorer;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.example.model.courserequest.CourseRegistrationRequestStatusType;
import org.example.model.courserequest.KeeperRejection;
import org.example.model.courserequest.RejectionReason;
import org.example.repository.ExplorerRepository;
import org.example.repository.courseregistration.CourseRegistrationRequestRepository;
import org.example.repository.courseregistration.CourseRegistrationRequestStatusRepository;
import org.example.repository.courseregistration.KeeperRejectionRepository;
import org.example.repository.courseregistration.RejectionReasonRepository;
import org.example.service.validator.CourseRegistrationRequestValidatorService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;
    private final KeeperRejectionRepository keeperRejectionRepository;
    private final RejectionReasonRepository rejectionReasonRepository;
    private final ExplorerRepository explorerRepository;

    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final CourseRegistrationRequestValidatorService courseRegistrationRequestValidatorService;

    @Transactional
    public CourseRegistrationRequest replyToRequest(Integer requestId, CourseRegistrationRequestReply requestReply) {
        CourseRegistrationRequest request = courseRegistrationRequestRepository
                .findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        courseRegistrationRequestValidatorService.validateRequest(request);
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

    private void addExplorer(Integer personId, Integer courseId) {
        sendGalaxyCacheRefreshMessage(courseId);
        explorerRepository.save(
                Explorer.builder()
                        .personId(personId)
                        .courseId(courseId)
                        .build()
        );
    }

    private void sendGalaxyCacheRefreshMessage(Integer courseId) {
        kafkaTemplate.send("galaxyCacheTopic", courseId);
    }

    public KeeperRejection sendRejection(Integer requestId, KeeperRejectionDTO rejection) {
        courseRegistrationRequestValidatorService.validateRejectionRequest(requestId);
        return keeperRejectionRepository.save(
                KeeperRejection.builder()
                        .requestId(requestId)
                        .reasonId(rejection.getReasonId())
                        .build()
        );
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasonRepository.findAll();
    }
}
