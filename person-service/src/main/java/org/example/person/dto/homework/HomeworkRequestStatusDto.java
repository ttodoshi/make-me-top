package org.example.person.dto.homework;

import lombok.Data;

@Data
public class HomeworkRequestStatusDto {
    private Integer statusId;
    private HomeworkRequestStatusType status;
}
