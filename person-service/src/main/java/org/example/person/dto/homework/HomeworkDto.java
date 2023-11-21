package org.example.person.dto.homework;

import lombok.Data;

@Data
public class HomeworkDto {
    private Integer homeworkId;
    private Integer courseThemeId;
    private String content;
    private Integer groupId;
}
