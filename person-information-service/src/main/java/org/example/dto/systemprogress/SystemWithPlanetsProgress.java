package org.example.dto.systemprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SystemWithPlanetsProgress {
    private Integer courseId;
    private String title;
    @JsonProperty("planets")
    private List<PlanetCompletionDTO> planetsWithProgress;
}
