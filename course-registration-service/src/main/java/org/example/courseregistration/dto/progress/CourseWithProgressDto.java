package org.example.courseregistration.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithProgressDto {
    private Long systemId;
    private Double progress;
}
