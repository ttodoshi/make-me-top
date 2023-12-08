package org.example.homework.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String content;
    private Long groupId;
}
