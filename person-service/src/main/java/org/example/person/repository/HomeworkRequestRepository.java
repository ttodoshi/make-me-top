package org.example.person.repository;

import org.example.person.dto.homework.HomeworkRequestDto;

import java.util.List;

public interface HomeworkRequestRepository {
    List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(List<Long> explorerIds);
}
