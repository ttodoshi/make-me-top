package org.example.repository;

import org.example.dto.homework.HomeworkRequestDto;

import java.util.List;

public interface HomeworkRequestRepository {
    List<HomeworkRequestDto> findOpenedHomeworkRequestsByExplorerIdIn(List<Integer> explorerIds);
}
