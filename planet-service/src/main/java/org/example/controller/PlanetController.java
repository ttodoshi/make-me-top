package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.planet.CreatePlanetDto;
import org.example.dto.planet.UpdatePlanetDto;
import org.example.service.PlanetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/planet-app")
@RequiredArgsConstructor
@Validated
public class PlanetController {
    private final PlanetService planetService;

    @GetMapping("/planets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get planets by system id in", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested planets",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findPlanetsBySystemIdIn(@RequestParam List<Integer> systemIds) {
        return ResponseEntity.ok(planetService.findPlanetsBySystemIdIn(systemIds));
    }

    @GetMapping("/systems/{systemId}/planets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get planets by system id", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested planets by system id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getPlanetsBySystemId(@PathVariable("systemId") Integer systemId) {
        return ResponseEntity.ok(planetService.findPlanetsListBySystemId(systemId));
    }

    @PostMapping("/systems/{systemId}/planets")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create planets", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Planets created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addPlanets(@RequestBody List<@Valid CreatePlanetDto> planetList,
                                        @PathVariable Integer systemId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        planetService.addPlanets(systemId, planetList)
                );
    }

    @PutMapping("/planets/{planetId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Update planet", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Planet updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updatePlanetById(@PathVariable("planetId") Integer planetId,
                                              @Valid @RequestBody UpdatePlanetDto planet) {
        return ResponseEntity.ok(planetService.updatePlanet(planetId, planet));
    }

    @DeleteMapping("/planets/{planetId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete planet", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Planet deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deletePlanetById(@PathVariable("planetId") Integer id) {
        return ResponseEntity.ok(planetService.deletePlanetById(id));
    }
}
