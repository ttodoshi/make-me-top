package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.dto.orbit.OrbitDto;
import org.example.service.OrbitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/galaxy-app")
@RequiredArgsConstructor
public class OrbitController {
    private final OrbitService orbitService;

    @GetMapping("/orbits/{orbitId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get orbit by id", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested orbit",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getOrbitById(@PathVariable("orbitId") Integer orbitId,
                                          @RequestParam(name = "withSystemList", required = false) Boolean withSystemsList) {
        if (withSystemsList != null && withSystemsList)
            return ResponseEntity.ok(orbitService.getOrbitWithSystemList(orbitId));
        else
            return ResponseEntity.ok(orbitService.getOrbitById(orbitId));
    }

    @PostMapping("/galaxies/{galaxyId}/orbits")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createOrbit(@Valid @RequestBody CreateOrbitWithStarSystemsDto orbit,
                                         @PathVariable Integer galaxyId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orbitService.createOrbit(galaxyId, orbit)
                );
    }

    @PutMapping("/orbits/{orbitId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Update orbit by id", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateOrbit(@PathVariable("orbitId") Integer orbitId,
                                         @Valid @RequestBody OrbitDto orbit) {
        return ResponseEntity.ok(orbitService.updateOrbit(orbitId, orbit));
    }

    @DeleteMapping("/orbits/{orbitId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete orbit by id", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteOrbit(@PathVariable("orbitId") Integer orbitId) {
        return ResponseEntity.ok(orbitService.deleteOrbit(orbitId));
    }
}
