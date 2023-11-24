package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.homework.exception.classes.request.StatusNotFoundException;
import org.example.homework.model.HomeworkRequestStatus;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.HomeworkRequestStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeworkRequestStatusService {
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;

    @Transactional(readOnly = true)
    public HomeworkRequestStatus findHomeworkRequestStatusByStatus(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository.findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> new StatusNotFoundException(status));
    }
}
