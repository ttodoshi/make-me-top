package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.course.CourseWithKeepers;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.model.CourseRegistrationRequest;
import org.example.model.CourseRegistrationRequestStatusType;
import org.example.model.Person;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.CourseRegistrationRequestStatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CourseRegistrationRequestService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRegistrationRequestStatusRepository courseRegistrationRequestStatusRepository;

    private final RestTemplate restTemplate;

    private final ModelMapper mapper;

    @Setter
    private String token;

    @Value("${get_course_by_id}")
    private String COURSE_BY_ID_URL;

    public CourseRegistrationRequest sendRequest(CreateCourseRegistrationRequest request) {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        CourseRegistrationRequest courseRegistrationRequest = mapper.map(request, CourseRegistrationRequest.class);
        courseRegistrationRequest.setRequestDate(new Date());
        courseRegistrationRequest.setStatusId(
                courseRegistrationRequestStatusRepository
                        .findCourseRegistrationRequestStatusByStatus(CourseRegistrationRequestStatusType.PROCESSING)
                        .orElseThrow(StatusNotFoundException::new).getStatusId());
        courseRegistrationRequest.setPersonId(authenticatedPersonId);
        if (!keeperExistsOnCourse(request.getKeeperId(), request.getCourseId()))
            throw new KeeperNotFoundException();
        return courseRegistrationRequestRepository.save(courseRegistrationRequest);
    }

    private boolean keeperExistsOnCourse(Integer keeperId, Integer courseId) {
        ResponseEntity<CourseWithKeepers> courseEntity = restTemplate.exchange(
                COURSE_BY_ID_URL + courseId,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                CourseWithKeepers.class);
        if (courseEntity.getStatusCode().equals(HttpStatus.OK) && courseEntity.getBody() != null)
            return courseEntity.getBody().getKeepers().stream().anyMatch(k -> k.getKeeperId().equals(keeperId));
        throw new CourseNotFoundException();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }
}
