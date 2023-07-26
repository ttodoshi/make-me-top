package org.example.dto.courseregistration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRegistrationRequestReply {
    @NotNull
    private Boolean approved;
}
