package org.example.homework.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.homework.exception.request.StatusNotFoundException;
import org.example.homework.model.HomeworkRequestStatus;
import org.example.homework.model.HomeworkRequestStatusType;
import org.example.homework.repository.HomeworkRequestStatusRepository;
import org.example.homework.service.HomeworkRequestStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkRequestStatusServiceImpl implements HomeworkRequestStatusService {
    private final HomeworkRequestStatusRepository homeworkRequestStatusRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeworkRequestStatus findHomeworkRequestStatusByStatus(HomeworkRequestStatusType status) {
        return homeworkRequestStatusRepository.findHomeworkRequestStatusByStatus(status)
                .orElseThrow(() -> {
                    log.error("status '{}' not found", status);
                    return new StatusNotFoundException(status);
                });
    }
}
