package org.example.dto.systemprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanetWithProgress {
    private Integer courseThemeId;
    private String title;
    private Integer progress;
}
