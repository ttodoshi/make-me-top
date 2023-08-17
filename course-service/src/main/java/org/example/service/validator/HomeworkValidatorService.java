package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.repository.CourseThemeRepository;
import org.example.repository.HomeworkRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeworkValidatorService {
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRepository homeworkRepository;

    public void validateGetRequest(Integer themeId) {
        themeExists(themeId);
    }

    public void validatePostRequest(Integer themeId) {
        themeExists(themeId);
    }

    public void validatePutRequest(Integer themeId) {
        themeExists(themeId);
    }

    private void themeExists(Integer themeId) {
        if (!courseThemeRepository.existsById(themeId))
            throw new CourseThemeNotFoundException(themeId);
    }

    public void validateDeleteRequest(Integer homeworkId) {
        if (!homeworkRepository.existsById(homeworkId))
            throw new HomeworkNotFoundException(homeworkId);
    }
}
