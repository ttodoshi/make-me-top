package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateHomeworkRequest {
    @NotBlank
    private String content;
}
