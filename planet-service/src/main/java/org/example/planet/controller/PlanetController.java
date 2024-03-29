package org.example.planet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.service.PlanetService;
import org.springframework.http.HttpHeaders;
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

    @GetMapping("/planets/{planetId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find planet by id", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested planet",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findPlanetById(@PathVariable Long planetId) {
        return ResponseEntity.ok(planetService.findPlanetById(planetId));
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
    public ResponseEntity<?> findPlanetsBySystemId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @PathVariable Long systemId) {
        return ResponseEntity.ok(
                planetService.findPlanetsBySystemId(authorizationHeader, systemId)
        );
    }

    @GetMapping("/planets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get planets by planet id in", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested planets",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findPlanetsByPlanetIdIn(@RequestParam List<Long> planetIds) {
        return ResponseEntity.ok(planetService.findPlanetsByPlanetIdIn(planetIds));
    }

    @GetMapping("/systems/planets")
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
    public ResponseEntity<?> findPlanetsBySystemIdIn(@RequestParam List<Long> systemIds) {
        return ResponseEntity.ok(planetService.findPlanetsBySystemIdIn(systemIds));
    }

    @PostMapping("/systems/{systemId}/planets")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.planet.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create planets", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Planets created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createPlanets(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                           @PathVariable Long systemId,
                                           @RequestBody List<@Valid CreatePlanetDto> planetList) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        planetService.createPlanets(authorizationHeader, systemId, planetList)
                );
    }

    @PutMapping("/planets/{planetId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.planet.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updatePlanetById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                              @PathVariable Long planetId,
                                              @Valid @RequestBody UpdatePlanetDto planet) {
        return ResponseEntity.ok(
                planetService.updatePlanet(authorizationHeader, planetId, planet)
        );
    }

    @DeleteMapping("/planets/{planetId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.planet.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> deletePlanetById(@PathVariable Long planetId) {
        return ResponseEntity.ok(planetService.deletePlanetById(planetId));
    }
}
