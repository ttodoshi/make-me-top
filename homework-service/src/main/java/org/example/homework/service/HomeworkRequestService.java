package org.example.homework.service;

import lombok.RequiredArgsConstructor;
import org.example.homework.model.HomeworkRequest;
import org.example.homework.repository.HomeworkRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkRequestService {
    private final HomeworkRequestRepository homeworkRequestRepository;

    @Transactional(readOnly = true)
    public List<HomeworkRequest> findOpenedHomeworkRequestsByExplorerIdIn(List<Integer> explorerIds) {
        return homeworkRequestRepository.findOpenedHomeworkRequestsByExplorerIdIn(explorerIds);
    }
}
