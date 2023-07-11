package org.example.dto.courseregistration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRegistrationRequest {
    private Integer courseId;
    private Integer keeperId;
}
