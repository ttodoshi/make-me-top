package org.example.homework.dto.homework;

import lombok.Data;

@Data
public class HomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String content;
    private Long groupId;
}
