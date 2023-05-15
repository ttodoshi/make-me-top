package org.example.model.baseModel;

import lombok.Data;

@Data
public abstract class OrbitBaseModel {
    private Integer orbitId;
    private Integer countSystem;
    private Integer levelOrbit;
    private Integer galaxyId;
}
