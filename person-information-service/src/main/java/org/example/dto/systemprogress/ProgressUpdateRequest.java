package org.example.dto.systemprogress;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProgressUpdateRequest {
    @NotNull
    private Integer progress;
}
