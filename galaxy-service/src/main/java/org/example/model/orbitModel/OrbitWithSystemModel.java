package org.example.model.orbitModel;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.model.baseModel.OrbitBaseModel;
import org.example.model.systemModel.SystemWithDependencyModel;

import java.util.List;

@Data
public class OrbitWithSystemModel extends OrbitBaseModel {
    @JsonProperty("systemList")
    List<SystemWithDependencyModel> systemWithDependencyModelList;
}
