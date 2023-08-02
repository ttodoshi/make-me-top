package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.HomeworkCreateRequest;
import org.example.dto.homework.HomeworkDTO;
import org.example.dto.homework.HomeworkUpdateRequest;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.model.homework.Homework;
import org.example.repository.HomeworkRepository;
import org.example.validator.HomeworkValidator;
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

    private final HomeworkValidator homeworkValidator;

    private final ModelMapper mapper;

    public List<HomeworkDTO> getHomeworkByThemeId(Integer themeId) {
        homeworkValidator.validateGetRequest(themeId);
        return homeworkRepository.findHomeworksByCourseThemeId(themeId)
                .stream().map(h -> mapper.map(h, HomeworkDTO.class)).collect(Collectors.toList());
    }

    public Homework addHomework(Integer themeId, HomeworkCreateRequest homework) {
        homeworkValidator.validatePostRequest(themeId);
        Homework createdHomework = new Homework();
        createdHomework.setContent(homework.getContent());
        createdHomework.setCourseThemeId(themeId);
        return homeworkRepository.save(createdHomework);
    }

    public Homework updateHomework(Integer homeworkId, HomeworkUpdateRequest homework) {
        homeworkValidator.validatePutRequest(homework.getCourseThemeId());
        Homework updatedHomework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        updatedHomework.setContent(homework.getContent());
        updatedHomework.setCourseThemeId(homework.getCourseThemeId());
        return homeworkRepository.save(updatedHomework);
    }

    public Map<String, String> deleteHomework(Integer homeworkId) {
        homeworkValidator.validateDeleteRequest(homeworkId);
        Map<String, String> response = new HashMap<>();
        homeworkRepository.deleteById(homeworkId);
        response.put("message", "Удалено задание " + homeworkId);
        return response;
    }
}
