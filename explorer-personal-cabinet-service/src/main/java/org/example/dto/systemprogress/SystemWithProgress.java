package org.example.dto.systemprogress;

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
