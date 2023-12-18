package org.example.courseregistration.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.courseregistration.dto.courserequest.CourseRegistrationRequestDto;
import org.example.courseregistration.exception.classes.courserequest.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;

    private final PersonService personService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public CourseRegistrationRequestDto findProcessingCourseRegistrationRequestByPersonId() {
        return courseRegistrationRequestRepository
                .findCourseRegistrationRequestByPersonIdAndStatus_NotAccepted(
                        personService.getAuthenticatedPersonId()
                ).map(r -> mapper.map(r, CourseRegistrationRequestDto.class))
                .orElseThrow(RequestNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(List<Long> requestIds) {
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
