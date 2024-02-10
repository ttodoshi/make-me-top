package org.example.homework.service;

import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestDto;

public interface ExplorerHomeworkRequestService {
    Long sendHomeworkRequest(String authorizationHeader, Long authenticatedPersonId, Long homeworkId, CreateHomeworkRequestDto request);
}
