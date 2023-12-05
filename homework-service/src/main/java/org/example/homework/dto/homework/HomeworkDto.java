package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.homework.model.HomeworkStatus;

@Data
public class HomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String content;
    private Long groupId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkStatus status;
    private Long statusId;
}
