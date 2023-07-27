package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class GalaxyCreateRequest extends GalaxyDTO {
    @NotNull
    List<@Valid OrbitWithStarSystemsCreateRequest> orbitList;
}
