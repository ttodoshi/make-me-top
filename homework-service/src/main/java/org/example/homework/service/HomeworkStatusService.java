package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.homework.exception.classes.request.StatusNotFoundException;
import org.example.homework.model.HomeworkStatus;
import org.example.homework.model.HomeworkStatusType;
import org.example.homework.repository.HomeworkStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeworkStatusService {
    private final HomeworkStatusRepository homeworkStatusRepository;

    @Transactional(readOnly = true)
    public HomeworkStatus findHomeworkStatusByStatus(HomeworkStatusType status) {
        return homeworkStatusRepository.findHomeworkStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status));
    }
}
