package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDto;
import org.example.dto.courseprogress.CourseThemeCompletionDto;
import org.example.model.progress.CourseMark;
import org.example.model.progress.CourseThemeCompletion;
import org.example.repository.courseprogress.CourseMarkRepository;
import org.example.repository.courseprogress.CourseThemeCompletionRepository;
import org.example.service.validator.MarkValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final MarkValidatorService markValidatorService;

    private final ModelMapper mapper;

    @Transactional
    public CourseMark setCourseMark(MarkDto courseMark) {
        markValidatorService.validateCourseMarkRequest(courseMark);
        return courseMarkRepository.save(
                mapper.map(courseMark, CourseMark.class)
        );
    }

    @Transactional
    public CourseThemeCompletion setThemeMark(Integer themeId, MarkDto mark) {
        markValidatorService.validateThemeMarkRequest(themeId, mark);
        return courseThemeCompletionRepository.save(
                mapper.map(
                        new CourseThemeCompletionDto(themeId, mark.getExplorerId(), mark.getValue()),
                        CourseThemeCompletion.class
                )
        );
    }
}
