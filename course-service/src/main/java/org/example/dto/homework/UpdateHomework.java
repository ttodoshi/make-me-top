package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@NotBlank
public class UpdateHomework {
    private Integer courseThemeId;
    private String content;
}
