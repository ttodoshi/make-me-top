package org.example.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCreateEvent {
    private Integer courseThemeId;
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
}
