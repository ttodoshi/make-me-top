package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.CreateGalaxyDto;
import org.example.dto.galaxy.GalaxyDto;
import org.example.service.GalaxyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/galaxy-app")
@RequiredArgsConstructor
public class GalaxyController {
    private final GalaxyService galaxyService;

    @GetMapping("/galaxies")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all galaxies", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All galaxies",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getAllGalaxies(@RequestParam(required = false) Boolean detailed) {
        if (detailed != null && detailed)
            return ResponseEntity.ok(galaxyService.getAllGalaxiesDetailed());
        return ResponseEntity.ok(galaxyService.getAllGalaxies());
    }

    @GetMapping("/galaxies/{galaxyId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get galaxy by id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested galaxy",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getGalaxyById(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(galaxyService.getGalaxyById(galaxyId));
    }

    @PostMapping("/galaxies")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createGalaxy(@Valid @RequestBody CreateGalaxyDto galaxy) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        galaxyService.createGalaxy(galaxy)
                );
    }

    @PutMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Update galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateGalaxy(@Valid @RequestBody GalaxyDto galaxy,
                                          @PathVariable("galaxyId") Integer id) {
        return ResponseEntity.ok(galaxyService.updateGalaxy(id, galaxy));
    }

    @DeleteMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteGalaxy(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(galaxyService.deleteGalaxy(galaxyId));
    }

    @GetMapping("/systems/{systemId}/galaxies")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get galaxy by system id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested galaxy",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getGalaxyBySystemId(@PathVariable("systemId") Integer systemId) {
        return ResponseEntity.ok(galaxyService.getGalaxyBySystemId(systemId));
    }
}
