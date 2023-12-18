package org.example.galaxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.starsystem.CreateStarSystemDto;
import org.example.galaxy.dto.starsystem.UpdateStarSystemDto;
import org.example.galaxy.service.StarSystemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/galaxy-app")
@RequiredArgsConstructor
public class StarSystemController {
    private final StarSystemService starSystemService;

    @GetMapping("/systems/{systemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested system",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findStarSystemById(@PathVariable Long systemId,
                                                @RequestParam(required = false) Boolean withDependencies) {
        if (withDependencies != null && withDependencies)
            return ResponseEntity.ok(starSystemService.findStarSystemByIdWithDependencies(systemId));
        else
            return ResponseEntity.ok(starSystemService.findStarSystemById(systemId));
    }

    @GetMapping("/galaxies/{galaxyId}/systems")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get systems by galaxy id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested systems by galaxy id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findStarSystemsByGalaxyId(@PathVariable Long galaxyId) {
        return ResponseEntity.ok(
                starSystemService.findStarSystemsByGalaxyId(galaxyId)
        );
    }

    @PostMapping("/orbits/{orbitId}/systems")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create system", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "System created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createSystem(@PathVariable Long orbitId,
                                          @Valid @RequestBody CreateStarSystemDto starSystem) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        starSystemService.createSystem(orbitId, starSystem)
                );
    }


    @PutMapping("/systems/{systemId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Update system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateSystem(@PathVariable Long systemId,
                                          @Valid @RequestBody UpdateStarSystemDto starSystem) {
        return ResponseEntity.ok(starSystemService.updateSystem(systemId, starSystem));
    }

    @DeleteMapping("/systems/{systemId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteSystem(@PathVariable Long systemId) {
        return ResponseEntity.ok(starSystemService.deleteSystem(systemId));
    }
}
