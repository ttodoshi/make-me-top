package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomework;
import org.example.dto.homework.HomeworkDTO;
import org.example.dto.homework.UpdateHomework;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.model.Homework;
import org.example.repository.CourseThemeRepository;
import org.example.repository.HomeworkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ModelMapper mapper;

    public List<HomeworkDTO> getHomeworkByThemeId(Integer themeId) {
        if (!courseThemeRepository.existsById(themeId))
            throw new CourseThemeNotFoundException(themeId);
        return homeworkRepository.findHomeworksByCourseThemeId(themeId)
                .stream().map(h -> mapper.map(h, HomeworkDTO.class)).collect(Collectors.toList());
    }

    public Homework addHomework(Integer themeId, CreateHomework homework) {
        if (!courseThemeRepository.existsById(themeId))
            throw new CourseThemeNotFoundException(themeId);
        Homework createdHomework = new Homework();
        createdHomework.setContent(homework.getContent());
        createdHomework.setCourseThemeId(themeId);
        return homeworkRepository.save(createdHomework);
    }

    public Homework updateHomework(Integer homeworkId, UpdateHomework homework) {
        if (!courseThemeRepository.existsById(homework.getCourseThemeId()))
            throw new CourseThemeNotFoundException(homework.getCourseThemeId());
        Homework updatedHomework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        updatedHomework.setContent(homework.getContent());
        updatedHomework.setCourseThemeId(homework.getCourseThemeId());
        return homeworkRepository.save(updatedHomework);
    }

    public Map<String, String> deleteHomework(Integer homeworkId) {
        Map<String, String> response = new HashMap<>();
        if (!homeworkRepository.existsById(homeworkId))
            throw new HomeworkNotFoundException(homeworkId);
        homeworkRepository.deleteById(homeworkId);
        response.put("message", "Удалено задание " + homeworkId);
        return response;
    }
}
