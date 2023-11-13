package org.example.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.event.CourseThemeCreateEvent;
import org.example.dto.theme.UpdateCourseThemeDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.CourseTheme;
import org.example.repository.CourseThemeRepository;
import org.example.service.validator.CourseThemeValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final CourseThemeValidatorService courseThemeValidatorService;

    private final ModelMapper mapper;

    private final KafkaTemplate<Integer, String> updatePlanetKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteExplorersProgressKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteHomeworksKafkaTemplate;

    public CourseThemeService(CourseThemeRepository courseThemeRepository, CourseThemeValidatorService courseThemeValidatorService,
                              ModelMapper mapper, @Qualifier("updatePlanetKafkaTemplate") KafkaTemplate<Integer, String> updatePlanetKafkaTemplate,
                              @Qualifier("deleteExplorersProgressKafkaTemplate") KafkaTemplate<Integer, Integer> deleteExplorersProgressKafkaTemplate,
                              @Qualifier("deleteHomeworksKafkaTemplate") KafkaTemplate<Integer, Integer> deleteHomeworksKafkaTemplate) {
        this.courseThemeRepository = courseThemeRepository;
        this.courseThemeValidatorService = courseThemeValidatorService;
        this.mapper = mapper;
        this.updatePlanetKafkaTemplate = updatePlanetKafkaTemplate;
        this.deleteExplorersProgressKafkaTemplate = deleteExplorersProgressKafkaTemplate;
        this.deleteHomeworksKafkaTemplate = deleteHomeworksKafkaTemplate;
    }

    @Transactional(readOnly = true)
    public CourseTheme getCourseTheme(Integer courseThemeId) {
        courseThemeValidatorService.validateGetThemeRequest(courseThemeId);
        return courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    @Transactional(readOnly = true)
    public Map<Integer, CourseTheme> findCourseThemesByCourseThemeIdIn(List<Integer> themeIds) {
        return courseThemeRepository.findCourseThemesByCourseThemeIdIn(themeIds)
                .stream()
                .collect(Collectors.toMap(
                        CourseTheme::getCourseThemeId,
                        t -> t
                ));
    }

    @Transactional(readOnly = true)
    public List<CourseTheme> getCourseThemesByCourseId(Integer courseId) {
        courseThemeValidatorService.validateGetThemesByCourseIdRequest(courseId);
        return courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumber(courseId);
    }

    @KafkaListener(topics = "createCourseThemeTopic", containerFactory = "createThemeKafkaListenerContainerFactory")
    @Transactional
    public CourseTheme createCourseTheme(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer courseId,
                                         @Payload CourseThemeCreateEvent courseThemeRequest) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        return courseThemeRepository.save(theme);
    }

    @KafkaListener(topics = "updateCourseThemeTopic", containerFactory = "updateThemeKafkaListenerContainerFactory")
    @Transactional
    public void updateCourseThemeTitle(ConsumerRecord<Integer, String> record) {
        CourseTheme courseTheme = courseThemeRepository.findById(record.key())
                .orElseThrow(() -> new CourseThemeNotFoundException(record.key()));
        courseTheme.setTitle(record.value());
        courseThemeRepository.save(courseTheme);
    }

    @Transactional
    public CourseTheme updateCourseTheme(Integer courseThemeId, UpdateCourseThemeDto courseTheme) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
        courseThemeValidatorService.validatePutRequest(courseThemeId, courseTheme);
        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        updatedTheme.setCourseThemeNumber(courseTheme.getCourseThemeNumber());
        updatePlanetName(courseThemeId, courseTheme.getTitle());
        return courseThemeRepository.save(updatedTheme);
    }

    private void updatePlanetName(Integer courseThemeId, String title) {
        updatePlanetKafkaTemplate.send("updatePlanetTopic", courseThemeId, title);
    }

    @KafkaListener(topics = "deleteCourseThemeTopic", containerFactory = "deleteThemeKafkaListenerContainerFactory")
    @Transactional
    public void deleteCourseTheme(Integer courseThemeId) {
        courseThemeRepository.deleteById(courseThemeId);
    }

    public void deleteDataRelatedToTheme(Integer themeId) {
        deleteHomeworksByThemeId(themeId);
        deleteExplorersProgressByThemeId(themeId);
    }

    private void deleteExplorersProgressByThemeId(Integer themeId) {
        deleteExplorersProgressKafkaTemplate.send("deleteExplorersProgressTopic", themeId);
    }

    private void deleteHomeworksByThemeId(Integer themeId) {
        deleteHomeworksKafkaTemplate.send("deleteHomeworksTopic", themeId);
    }

    public void deleteDataRelatedToThemes(List<Integer> themeIds) {
        themeIds.forEach(this::deleteDataRelatedToTheme);
    }
}
