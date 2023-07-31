package org.example.dto.courseprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithProgress {
    private Integer systemId;
    private Double progress;
}
