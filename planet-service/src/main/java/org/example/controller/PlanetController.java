package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.planet.CreatePlanet;
import org.example.dto.planet.PlanetUpdateRequest;
import org.example.service.PlanetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/planet-app/")
@RequiredArgsConstructor
@Validated
public class PlanetController {
    private final PlanetService planetService;

    @GetMapping("system/{systemId}/planet")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get planets by system id", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Planets by system id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getPlanetsBySystemId(@PathVariable("systemId") Integer systemId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.getPlanetsListBySystemId(systemId));
    }

    @PostMapping("system/{systemId}/planet")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create planets", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Create planets",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addPlanets(@RequestBody List<@Valid CreatePlanet> planetList,
                                        @PathVariable Integer systemId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.addPlanets(systemId, planetList));
    }

    @PutMapping("planet/{planetId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update planet", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Update planet",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updatePlanetById(@PathVariable("planetId") Integer planetId,
                                              @Valid @RequestBody PlanetUpdateRequest planet) {
        return ResponseEntity.ok(planetService.updatePlanet(planet, planetId));
    }

    @DeleteMapping("planet/{planetId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete planet", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete planet",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deletePlanetById(@PathVariable("planetId") Integer id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.deletePlanetById(id));
    }
}
