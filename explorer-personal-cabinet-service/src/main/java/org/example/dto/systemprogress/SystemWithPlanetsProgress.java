package org.example.dto.systemprogress;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SystemWithPlanetsProgress {
    private Integer courseId;
    private String title;
    private List<PlanetWithProgress> planetsWithProgress;
}
