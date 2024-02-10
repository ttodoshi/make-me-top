package org.example.homework.service;

import org.example.homework.model.HomeworkRequestStatus;
import org.example.homework.model.HomeworkRequestStatusType;

public interface HomeworkRequestStatusService {
    HomeworkRequestStatus findHomeworkRequestStatusByStatus(HomeworkRequestStatusType status);
}
