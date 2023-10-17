package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.HomeworkRequest;
import org.example.repository.HomeworkRequestRepository;
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
