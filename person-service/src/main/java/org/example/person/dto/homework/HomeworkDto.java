package org.example.person.dto.homework;

import lombok.Data;

@Data
public class HomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String content;
    private Long groupId;
}
