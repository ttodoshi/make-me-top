package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HomeworkCreateRequest {
    @NotBlank
    private String content;
}
