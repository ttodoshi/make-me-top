package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.event.CourseThemeCreateEvent;
import org.example.dto.theme.GetCourseThemeDto;
import org.example.dto.theme.UpdateCourseThemeDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.course.CourseTheme;
import org.example.repository.course.CourseThemeRepository;
import org.example.service.validator.CourseThemeValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidatorService courseThemeValidatorService;

    private final ModelMapper mapper;

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        courseThemeValidatorService.validateGetThemeRequest(courseThemeId);
        return courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    @Transactional(readOnly = true)
    public List<GetCourseThemeDto> getCourseThemesByCourseId(Integer courseId) {
        courseThemeValidatorService.validateGetThemesByCourseIdRequest(courseId);
        return courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumber(courseId)
                .stream()
                .map(t -> mapper.map(t, GetCourseThemeDto.class))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "courseThemeTopic", containerFactory = "themeKafkaListenerContainerFactory")
    public CourseTheme createCourseTheme(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer courseId,
                                         @Payload CourseThemeCreateEvent courseThemeRequest) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        return courseThemeRepository.save(theme);
    }

    @Transactional
    public CourseTheme updateCourseTheme(Integer courseThemeId, UpdateCourseThemeDto courseTheme) {
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
