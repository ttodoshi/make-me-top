package org.example.progress.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExplorerProgressDto {
    private Integer explorerId;
    private Integer groupId;
    private Integer currentThemeId;
    private CourseWithThemesProgressDto progress;
}
