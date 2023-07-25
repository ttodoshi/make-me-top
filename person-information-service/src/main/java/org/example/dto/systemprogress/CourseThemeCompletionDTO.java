package org.example.dto.systemprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCompletionDTO {
    private Integer explorerId;
    private Integer courseThemeId;
    private Boolean completed;
}
