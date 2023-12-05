package org.example.homework.dto.homeworkrequest;

import lombok.Data;
import org.example.homework.model.HomeworkRequestStatusType;

@Data
public class HomeworkRequestStatusDto {
    private Long statusId;
    private HomeworkRequestStatusType status;
}
