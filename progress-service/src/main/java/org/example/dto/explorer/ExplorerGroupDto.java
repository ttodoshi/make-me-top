package org.example.dto.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExplorerGroupDto {
    private Integer groupId;
    private Integer courseId;
    private Integer keeperId;
}
