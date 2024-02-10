package org.example.person.service.api.homework;

import org.example.person.dto.homework.HomeworkRequestDto;

import java.util.List;

public interface HomeworkRequestService {
    List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(String authorizationHeader, List<Long> explorerIds);
}
