package org.example.homework.service;

import org.example.homework.dto.homework.GetHomeworkWithRequestDto;
import org.example.homework.dto.homeworkrequest.HomeworkRequestDto;
import org.example.homework.model.HomeworkRequest;

import java.util.List;

public interface HomeworkRequestService {
    HomeworkRequest findHomeworkRequestById(Long requestId);

    List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(String authorizationHeader, Long authenticatedPersonId, List<Long> explorerIds);

    GetHomeworkWithRequestDto findHomeworkWithRequestByHomeworkId(String authorizationHeader, Long authenticatedPersonId, Long homeworkId);

    GetHomeworkWithRequestDto findHomeworkWithRequestByRequestId(String authorizationHeader, Long authenticatedPersonId, Long requestId);
}
