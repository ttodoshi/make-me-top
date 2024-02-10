package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.course.dto.event.CourseThemeUpdateEvent;
import org.example.course.dto.theme.CourseThemeDto;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.course.exception.theme.CourseThemeNotFoundException;
import org.example.course.model.CourseTheme;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.service.CourseThemeService;
import org.example.course.service.validator.CourseThemeValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseThemeServiceImpl implements CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidatorService courseThemeValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CourseThemeDto findCourseThemeById(String authorizationHeader, Authentication authentication, Long courseThemeId) {
        courseThemeValidatorService.validateGetThemeRequest(authorizationHeader, authentication, courseThemeId);
        return courseThemeRepository.findById(courseThemeId)
                .map(t -> mapper.map(t, CourseThemeDto.class))
                .orElseThrow(() -> {
                    log.warn("course theme by id {} not found", courseThemeId);
                    return new CourseThemeNotFoundException(courseThemeId);
                });
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
                .orElseThrow(() -> {
                    log.warn("course theme by id {} not found", themeId);
                    return new CourseThemeNotFoundException(themeId);
                });

        courseTheme.setTitle(courseThemeRequest.getTitle());
        courseTheme.setCourseThemeNumber(courseThemeRequest.getCourseThemeNumber());
        courseTheme.setCourseId(courseThemeRequest.getCourseId());
    }

    @Override
    @Transactional
    public CourseThemeDto updateCourseTheme(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> {
                    log.warn("course theme by id {} not found", courseThemeId);
                    return new CourseThemeNotFoundException(courseThemeId);
                });

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

    @Override
    @KafkaListener(topics = "deleteCourseThemeTopic", containerFactory = "deleteThemeKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseTheme(Long courseThemeId) {
        courseThemeRepository.deleteById(courseThemeId);
    }
}
