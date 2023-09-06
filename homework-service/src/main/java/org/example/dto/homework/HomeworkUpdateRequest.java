package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HomeworkUpdateRequest {
    @NotNull
    private Integer courseThemeId;
    @NotBlank
    private String content;
    @NotNull
    private Integer groupId;
}
