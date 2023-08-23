package org.example.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRegistrationRequest {
    @NotNull
    private Integer courseId;
    @NotNull
    private Integer keeperId;
}
