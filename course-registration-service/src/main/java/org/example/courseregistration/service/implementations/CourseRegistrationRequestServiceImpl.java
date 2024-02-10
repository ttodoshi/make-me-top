package org.example.courseregistration.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.courseregistration.config.security.RoleService;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestDto;
import org.example.courseregistration.enums.CourseRoleType;
import org.example.courseregistration.exception.courserequest.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.CourseRegistrationRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestServiceImpl implements CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;

    private final RoleService roleService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CourseRegistrationRequestDto findProcessingCourseRegistrationRequestByPersonId(Long personId) {
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(
                        personId
                ).map(r -> mapper.map(r, CourseRegistrationRequestDto.class))
                .orElseThrow(RequestNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(String authorizationHeader, Long personId, List<Long> requestIds) {
        if (!roleService.hasAnyCourseRoleByRequestIds(authorizationHeader, personId, requestIds, CourseRoleType.KEEPER)) {
            log.warn("access denied");
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestsByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(
                        CourseRegistrationRequest::getRequestId,
                        r -> mapper.map(r, CourseRegistrationRequestDto.class)
                ));
    }

    @KafkaListener(topics = "deleteCourseRegistrationRequestIfPresent", containerFactory = "deleteCourseRegistrationRequestIfPresentKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseRegistrationRequestByCourseIdAndPersonIdIfPresent(ConsumerRecord<Long, Long> record) {
        courseRegistrationRequestRepository
                .findCourseRegistrationRequestByCourseIdAndPersonIdAndStatus_NotAccepted(record.key(), record.value())
                .ifPresent(courseRegistrationRequestRepository::delete);
    }

    @KafkaListener(topics = "deleteCourseRegistrationRequestsTopic", containerFactory = "deleteCourseRegistrationRequestsKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseRegistrationRequestsByCourseId(Long courseId) {
        courseRegistrationRequestRepository
                .deleteCourseRegistrationRequestsByCourseId(courseId);
    }
}
