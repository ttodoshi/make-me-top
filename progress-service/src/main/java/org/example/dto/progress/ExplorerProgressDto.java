package org.example.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExplorerProgressDto {
    private Integer explorerId;
    private Integer groupId;
    private CourseWithThemesProgressDto progress;
}
