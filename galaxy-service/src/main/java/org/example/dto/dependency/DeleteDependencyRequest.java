package org.example.dto.dependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"isAlternative"})
public class DeleteDependencyRequest extends DependencyDTO {

}
