package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeCreateRequest;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeAlreadyExistsException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.CourseTheme;
import org.example.repository.CourseRepository;
import org.example.repository.CourseThemeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ModelMapper mapper;

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        return courseThemeRepository.findById(courseThemeId).orElseThrow(CourseThemeNotFoundException::new);
    }

    public List<CourseTheme> getCourseThemesByCourseId(Integer courseId) {
        if (courseRepository.existsById(courseId))
            return courseThemeRepository.findCourseThemesByCourseId(courseId);
        throw new CourseNotFoundException();
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
        CourseTheme updatedTheme = courseThemeRepository.findById(courseThemeId).orElseThrow(CourseThemeNotFoundException::new);
        boolean themeExists = courseThemeRepository.findCourseThemesByCourseId(updatedTheme.getCourseId()).stream().anyMatch(
                t -> t.getTitle().equals(updatedTheme.getTitle())
        );
        if (themeExists)
            throw new CourseThemeAlreadyExistsException();
        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setLastModified(new Date());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        updatedTheme.setCourseThemeNumber(courseTheme.getCourseThemeNumber());
        return courseThemeRepository.save(updatedTheme);
    }
}
