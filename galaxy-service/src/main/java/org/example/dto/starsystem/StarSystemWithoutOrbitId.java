package org.example.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties("orbitId")
public class StarSystemWithoutOrbitId extends StarSystemRequest {

}
