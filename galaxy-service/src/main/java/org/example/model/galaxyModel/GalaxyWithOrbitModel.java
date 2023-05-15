package org.example.model.galaxyModel;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.model.baseModel.GalaxyBaseModel;
import org.example.model.orbitModel.OrbitWithSystemModel;

import java.util.List;

@Data
@JsonIgnoreProperties("galaxyDescription")
public class GalaxyWithOrbitModel extends GalaxyBaseModel {
    @JsonProperty("orbitList")
    private List<OrbitWithSystemModel> orbitsList;
}
