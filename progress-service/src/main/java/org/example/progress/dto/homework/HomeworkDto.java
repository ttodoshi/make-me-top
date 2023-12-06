package org.example.progress.dto.homework;

import lombok.Data;

@Data
public class HomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String content;
    private Long groupId;
}
