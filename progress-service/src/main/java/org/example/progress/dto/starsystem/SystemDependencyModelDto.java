package org.example.progress.dto.starsystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDependencyModelDto {
    private Long systemId;
    private String type;
    private Boolean isAlternative;
}
