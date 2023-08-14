package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.courseprogress.CourseThemeProgressDTO;
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

    public CourseMark setCourseMark(MarkDTO courseMark) {
        markValidatorService.validateCourseMarkRequest(courseMark);
        return courseMarkRepository.save(
                mapper.map(courseMark, CourseMark.class)
        );
    }

    public CourseThemeCompletion setThemeMark(Integer themeId, MarkDTO mark) {
        markValidatorService.validateThemeMarkRequest(themeId, mark);
        return courseThemeCompletionRepository.save(
                mapper.map(
                        new CourseThemeProgressDTO(
                                mark.getExplorerId(), themeId, mark.getValue()),
                        CourseThemeCompletion.class));
    }
}
