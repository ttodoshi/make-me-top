package org.example.dto.coursemark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkDTO {
    @NotNull
    private Integer explorerId;
    @NotNull
    private Integer value;
}
