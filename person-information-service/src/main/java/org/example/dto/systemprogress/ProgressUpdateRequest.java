package org.example.dto.systemprogress;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class ProgressUpdateRequest {
    private Integer progress;
}
