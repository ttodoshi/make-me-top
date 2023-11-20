package org.example.courseregistration.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRegistrationRequestDto {
    @NotNull
    private Integer courseId;
    @NotEmpty
    private List<@Valid Integer> keeperIds;
}
