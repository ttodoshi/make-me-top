package org.example.dto.orbit;

import lombok.Data;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
public class OrbitWithStarSystemsCreateRequest {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Integer systemCount;
    @NotNull
    private List<@Valid StarSystemCreateRequest> systemList;
}
