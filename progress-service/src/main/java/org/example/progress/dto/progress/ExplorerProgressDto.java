package org.example.progress.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExplorerProgressDto {
    private Long explorerId;
    private Long groupId;
    private Long currentThemeId;
    private CourseWithThemesProgressDto progress;
}
