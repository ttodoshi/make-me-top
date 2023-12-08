package org.example.homework.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateHomeworkDto {
    @NotNull
    private Long courseThemeId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Long groupId;
}
