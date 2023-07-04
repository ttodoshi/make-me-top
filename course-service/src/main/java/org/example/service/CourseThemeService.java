package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CourseThemeCreateRequest;
import org.example.dto.CourseThemeUpdateRequest;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.CourseTheme;
import org.example.repository.CourseThemeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CourseThemeService {
    private final CourseThemeRepository courseThemeRepository;

    private final ModelMapper mapper;

    private final Logger logger = Logger.getLogger(CourseThemeService.class.getName());

    public CourseTheme getCourseTheme(Integer courseThemeId) {
        return courseThemeRepository.findById(courseThemeId).orElseThrow(CourseThemeNotFoundException::new);
    }

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
        updatedTheme.setCourseId(courseTheme.getCourseId());
        updatedTheme.setTitle(courseTheme.getTitle());
        updatedTheme.setLastModified(new Date());
        updatedTheme.setDescription(courseTheme.getDescription());
        updatedTheme.setContent(courseTheme.getContent());
        return courseThemeRepository.save(updatedTheme);
    }

    public Map<String, String> deleteCourseTheme(Integer courseThemeId) {
        try {
            courseThemeRepository.deleteById(courseThemeId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Тема курса " + courseThemeId + " была удалёна");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new CourseThemeNotFoundException();
        }
    }
}
