package org.example.courseregistration.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateKeeperRejectionDto {
    @NotNull
    private Integer reasonId;
}
