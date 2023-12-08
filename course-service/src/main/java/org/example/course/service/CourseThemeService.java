package org.example.course.service;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.course.dto.event.CourseThemeUpdateEvent;
import org.example.course.dto.theme.CourseThemeDto;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.course.exception.classes.theme.CourseThemeNotFoundException;
import org.example.course.model.CourseTheme;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.service.validator.CourseThemeValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidatorService courseThemeValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public CourseThemeDto findCourseThemeById(Long courseThemeId) {
        courseThemeValidatorService.validateGetThemeRequest(courseThemeId);
        return courseThemeRepository.findById(courseThemeId)
                .map(t -> mapper.map(t, CourseThemeDto.class))
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    @KafkaListener(topics = "createCourseThemeTopic", containerFactory = "createThemeKafkaListenerContainerFactory")
    @Transactional
    public void createCourseTheme(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long courseId,
                                  @Payload CourseThemeCreateEvent courseThemeRequest) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        courseThemeRepository.save(theme);
    }

    @KafkaListener(topics = "updateCourseThemeTopic", containerFactory = "updateThemeKafkaListenerContainerFactory")
    @Transactional
    public void updateCourseThemeListener(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long themeId,
                                          @Payload CourseThemeUpdateEvent courseThemeRequest) {
        CourseTheme courseTheme = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId));

        courseTheme.setTitle(courseThemeRequest.getTitle());
        courseTheme.setCourseThemeNumber(courseThemeRequest.getCourseThemeNumber());
        courseTheme.setCourseId(courseThemeRequest.getCourseId());
        courseThemeRepository.save(courseTheme);
    }

    @Transactional
    public CourseThemeDto updateCourseTheme(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));

        courseThemeValidatorService.validatePutRequest(courseThemeId, courseTheme);

        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        updatedTheme.setCourseThemeNumber(courseTheme.getCourseThemeNumber());

        return mapper.map(
                courseThemeRepository.save(updatedTheme),
                CourseThemeDto.class
        );
    }

    @KafkaListener(topics = "deleteCourseThemeTopic", containerFactory = "deleteThemeKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseTheme(Long courseThemeId) {
        courseThemeRepository.deleteById(courseThemeId);
    }
}
