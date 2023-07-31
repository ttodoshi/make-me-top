package org.example.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.model.homework.HomeworkResponse;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequest {
    private Integer requestId;
    private Integer homeworkId;
    private String content;
    private Integer keeperId;
    private Integer explorerId;
    private Date requestDate;
    private Integer statusId;
    private HomeworkRequestStatusType status;
    private List<HomeworkResponse> responses;
}
