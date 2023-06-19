package org.example.dto.dependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties("systemId")
public class DependencyGetInfoModel {
    private Integer systemId;
    private String type;
    private Boolean isAlternative;
}
