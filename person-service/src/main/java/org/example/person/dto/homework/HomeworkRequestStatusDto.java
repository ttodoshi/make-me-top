package org.example.person.dto.homework;

import lombok.Data;

@Data
public class HomeworkRequestStatusDto {
    private Long statusId;
    private HomeworkRequestStatusType status;
}
