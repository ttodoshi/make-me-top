package org.example.dto.dependency;

import lombok.Data;

@Data
public class DependencyGetInfoModel {
    private Integer systemId;
    private String type;
    private Boolean isAlternative;
}
