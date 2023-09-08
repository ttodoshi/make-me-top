package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkDto;
import org.example.dto.homework.UpdateHomeworkDto;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.model.homework.Homework;
import org.example.repository.homework.HomeworkRepository;
import org.example.service.validator.HomeworkValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;

    private final HomeworkValidatorService homeworkValidatorService;

    @Transactional(readOnly = true)
    public List<Homework> getHomeworkByThemeIdForGroup(Integer themeId, Integer groupId) {
        homeworkValidatorService.validateGetRequest(themeId, groupId);
        return homeworkRepository.findHomeworksByCourseThemeIdAndGroupId(themeId, groupId);
    }

    public Homework addHomework(Integer themeId, CreateHomeworkDto homework) {
        homeworkValidatorService.validatePostRequest(themeId, homework.getGroupId());
        return homeworkRepository.save(
                new Homework(themeId, homework.getContent(), homework.getGroupId())
        );
    }

    public Homework updateHomework(Integer homeworkId, UpdateHomeworkDto homework) {
        homeworkValidatorService.validatePutRequest(homework);
        Homework updatedHomework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        updatedHomework.setContent(homework.getContent());
        updatedHomework.setCourseThemeId(homework.getCourseThemeId());
        updatedHomework.setGroupId(homework.getGroupId());
        return homeworkRepository.save(updatedHomework);
    }

    public Map<String, String> deleteHomework(Integer homeworkId) {
        homeworkValidatorService.validateDeleteRequest(homeworkId);
        Map<String, String> response = new HashMap<>();
        homeworkRepository.deleteById(homeworkId);
        response.put("message", "Удалено задание " + homeworkId);
        return response;
    }
}
