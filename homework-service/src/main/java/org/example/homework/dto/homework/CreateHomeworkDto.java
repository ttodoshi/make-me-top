package org.example.homework.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateHomeworkDto {
    @NotNull
    private Long groupId;
    @NotBlank
    private String content;
}
