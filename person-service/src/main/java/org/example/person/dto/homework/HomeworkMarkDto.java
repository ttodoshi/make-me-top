package org.example.person.dto.homework;

import lombok.Data;

@Data
public class HomeworkMarkDto {
    private Long requestId;
    private Integer mark;
    private String comment;
}
