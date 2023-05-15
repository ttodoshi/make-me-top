package org.example.model.systemModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.model.baseModel.SystemBaseModel;
import org.example.model.dependencyModel.DependencyGetInfoModel;

import java.util.List;

@Data
@JsonIgnoreProperties("orbitId")
public class SystemWithDependencyModel extends SystemBaseModel {
    @JsonProperty("systemDependencyList")
    private List<DependencyGetInfoModel> dependencyList;

    private Integer positionSystem;
}
