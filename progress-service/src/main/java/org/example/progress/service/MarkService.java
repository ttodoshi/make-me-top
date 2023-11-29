package org.example.progress.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.exception.classes.mark.CourseMarkNotFoundException;
import org.example.progress.model.CourseMark;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.service.validator.MarkValidatorService;
import org.example.progress.repository.CourseMarkRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final MarkValidatorService markValidatorService;

    public CourseMark getCourseMark(Long explorerId) {
        return courseMarkRepository.findById(explorerId)
                .orElseThrow(() -> new CourseMarkNotFoundException(explorerId));
    }

    @Transactional
    public CourseMark setCourseMark(MarkDto mark) {
        markValidatorService.validateCourseMarkRequest(mark);
        return courseMarkRepository.save(
                new CourseMark(mark.getExplorerId(), mark.getValue())
        );
    }

    @Transactional
    public CourseThemeCompletion setThemeMark(Long themeId, MarkDto mark) {
        markValidatorService.validateThemeMarkRequest(themeId, mark);
        return courseThemeCompletionRepository.save(
                new CourseThemeCompletion(mark.getExplorerId(), themeId, mark.getValue())
        );
    }

    @KafkaListener(topics = "deleteExplorersProgressTopic", containerFactory = "deleteExplorersProgressKafkaListenerContainerFactory")
    @Transactional
    public void deleteExplorersProgressByThemeId(Long themeId) {
        courseThemeCompletionRepository
                .deleteCourseThemeCompletionsByCourseThemeId(themeId);
    }

    @KafkaListener(topics = "deleteProgressAndMarkTopic", containerFactory = "deleteProgressAndMarkKafkaListenerContainerFactory")
    @Transactional
    public void deleteProgressAndMarkByExplorerId(Long explorerId) {
        courseThemeCompletionRepository
                .deleteCourseThemeCompletionsByExplorerId(explorerId);
        if (courseMarkRepository.existsById(explorerId))
            courseMarkRepository.deleteById(explorerId);
    }
}
