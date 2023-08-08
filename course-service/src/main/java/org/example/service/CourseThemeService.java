package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.event.CourseThemeCreateEvent;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.course.CourseTheme;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.example.validator.CourseThemeValidator;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidator courseThemeValidator;

    private final ModelMapper mapper;

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        return courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    public List<CourseTheme> getCourseThemesByCourseId(Integer courseId) {
        courseThemeValidator.validateGetThemesByCourseIdRequest(courseId);
        return courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumber(courseId);
    }

    @KafkaListener(topics = "courseThemeTopic", containerFactory = "themeKafkaListenerContainerFactory")
    @Transactional
    public CourseTheme createCourseTheme(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer courseId,
                                         @Payload CourseThemeCreateEvent courseThemeRequest) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        return courseThemeRepository.save(theme);
    }

    public CourseTheme updateCourseTheme(Integer courseThemeId,
                                         CourseThemeUpdateRequest courseTheme) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
        courseThemeValidator.validatePutRequest(courseThemeId, courseTheme);
        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        updatedTheme.setCourseThemeNumber(courseTheme.getCourseThemeNumber());
        return courseThemeRepository.save(updatedTheme);
    }
}
