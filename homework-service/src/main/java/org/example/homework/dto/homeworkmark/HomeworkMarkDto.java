package org.example.homework.dto.homeworkmark;

import lombok.Data;

@Data
public class HomeworkMarkDto {
    private Long requestId;
    private Integer mark;
    private String comment;
}
