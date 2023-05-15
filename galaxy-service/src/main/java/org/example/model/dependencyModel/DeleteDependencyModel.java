package org.example.model.dependencyModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.model.baseModel.DependencyBaseModel;

@Data
@JsonIgnoreProperties({"dependencyId", "isAlternative"})
public class DeleteDependencyModel extends DependencyBaseModel {

}
