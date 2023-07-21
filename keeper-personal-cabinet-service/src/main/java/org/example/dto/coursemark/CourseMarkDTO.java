package org.example.dto.coursemark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class CourseMarkDTO {
    private Integer explorerId;
    private Integer value;
}
