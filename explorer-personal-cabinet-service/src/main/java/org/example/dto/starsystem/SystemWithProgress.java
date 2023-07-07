package org.example.dto.starsystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemWithProgress {
    private Integer systemId;
    private Double progress;
}
