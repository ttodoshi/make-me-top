package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HomeworkCreateRequest {
    @NotNull
    private Integer groupId;
    @NotBlank
    private String content;
}
