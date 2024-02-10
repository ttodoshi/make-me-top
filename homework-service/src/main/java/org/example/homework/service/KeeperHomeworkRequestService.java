package org.example.homework.service;

import org.example.homework.dto.homeworkmark.CreateHomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestFeedbackDto;

public interface KeeperHomeworkRequestService {
    Long setHomeworkMark(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateHomeworkMarkDto mark);

    Long sendHomeworkRequestFeedback(String authorizationHeader, Long authenticatedPersonId, Long requestId, CreateHomeworkRequestFeedbackDto feedback);
}
