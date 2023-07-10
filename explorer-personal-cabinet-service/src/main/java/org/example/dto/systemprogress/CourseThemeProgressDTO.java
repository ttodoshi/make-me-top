package org.example.dto.systemprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeProgressDTO {
    private Integer explorerId;
    private Integer courseThemeId;
    private Integer progress;
}
