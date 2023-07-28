package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeCreateRequest;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeAlreadyExistsException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.course.CourseTheme;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ModelMapper mapper;

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        return courseThemeRepository.findById(courseThemeId).orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
    }

    public List<CourseTheme> getCourseThemesByCourseId(Integer courseId) {
        if (courseRepository.existsById(courseId))
            return courseThemeRepository.findCourseThemesByCourseId(courseId);
        throw new CourseNotFoundException(courseId);
    }

    @Transactional
    public CourseTheme createCourseTheme(CourseThemeCreateRequest courseThemeRequest,
                                         Integer courseId) {
        CourseTheme theme = mapper.map(courseThemeRequest, CourseTheme.class);
        theme.setCourseId(courseId);
        theme.setLastModified(new Date());
        return courseThemeRepository.save(theme);
    }

    public CourseTheme updateCourseTheme(CourseThemeUpdateRequest courseTheme,
                                         Integer courseThemeId) {
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(courseThemeId));
        if (!courseRepository.existsById(courseTheme.getCourseId()))
            throw new CourseNotFoundException(courseTheme.getCourseId());
        boolean themeTitleExists = courseThemeRepository.findCourseThemesByCourseId(
                        updatedTheme.getCourseId()).stream()
                .anyMatch(t -> t.getTitle().equals(updatedTheme.getTitle()) && !t.getCourseThemeId().equals(courseThemeId));
        if (themeTitleExists)
            throw new CourseThemeAlreadyExistsException(updatedTheme.getTitle());
        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        updatedTheme.setCourseThemeNumber(courseTheme.getCourseThemeNumber());
        return courseThemeRepository.save(updatedTheme);
    }
}
