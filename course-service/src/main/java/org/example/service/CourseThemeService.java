package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.dto.event.CourseThemeCreateEvent;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.course.CourseTheme;
import org.example.repository.CourseThemeRepository;
import org.example.service.validator.CourseThemeValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidatorService courseThemeValidatorService;

    private final ModelMapper mapper;

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        return courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    public List<CourseTheme> getCourseThemesByCourseId(Integer courseId) {
        courseThemeValidatorService.validateGetThemesByCourseIdRequest(courseId);
        return courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumber(courseId);
    }

    @KafkaListener(topics = "courseThemeTopic", containerFactory = "themeKafkaListenerContainerFactory")
    public CourseTheme createCourseTheme(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer courseId,
                                         @Payload CourseThemeCreateEvent courseThemeRequest) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        return courseThemeRepository.save(theme);
    }

    public CourseTheme updateCourseTheme(Integer courseThemeId, CourseThemeUpdateRequest courseTheme) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
        courseThemeValidatorService.validatePutRequest(courseThemeId, courseTheme);
        return courseThemeRepository.save(
                updatedTheme
                        .withCourseId(courseTheme.getCourseId())
                        .withTitle(courseTheme.getTitle())
                        .withDescription(courseTheme.getDescription())
                        .withContent(courseTheme.getContent())
                        .withCourseThemeNumber(courseTheme.getCourseThemeNumber())
        );
    }
}
