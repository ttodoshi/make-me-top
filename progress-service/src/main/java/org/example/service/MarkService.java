package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.mark.MarkDto;
import org.example.model.CourseMark;
import org.example.model.CourseThemeCompletion;
import org.example.repository.CourseMarkRepository;
import org.example.repository.CourseThemeCompletionRepository;
import org.example.service.validator.MarkValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final MarkValidatorService markValidatorService;

    @Transactional
    public CourseMark setCourseMark(MarkDto mark) {
        markValidatorService.validateCourseMarkRequest(mark);
        return courseMarkRepository.save(
                new CourseMark(mark.getExplorerId(), mark.getValue())
        );
    }

    @Transactional
    public CourseThemeCompletion setThemeMark(Integer themeId, MarkDto mark) {
        markValidatorService.validateThemeMarkRequest(themeId, mark);
        return courseThemeCompletionRepository.save(
                new CourseThemeCompletion(mark.getExplorerId(), themeId, mark.getValue())
        );
    }
}
