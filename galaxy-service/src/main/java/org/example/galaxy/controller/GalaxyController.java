package org.example.galaxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.galaxy.CreateGalaxyDto;
import org.example.galaxy.dto.galaxy.UpdateGalaxyDto;
import org.example.galaxy.service.GalaxyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/galaxy-app")
@RequiredArgsConstructor
public class GalaxyController {
    private final GalaxyService galaxyService;

    @GetMapping("/galaxies/{galaxyId}")
    @Operation(summary = "Find galaxy by id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested galaxy",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findGalaxyById(@PathVariable Long galaxyId, @RequestParam(required = false) Boolean detailed) {
        if (detailed != null && detailed)
            return ResponseEntity.ok(galaxyService.findGalaxyByIdDetailed(galaxyId));
        return ResponseEntity.ok(galaxyService.findGalaxyById(galaxyId));
    }

    @GetMapping("/galaxies")
    @Operation(summary = "Find all galaxies", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All galaxies",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getAllGalaxies() {
        return ResponseEntity.ok(galaxyService.findAllGalaxies());
    }

    @GetMapping(value = "/systems/{systemId}/galaxies")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find galaxy by system id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested galaxy",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findGalaxyBySystemId(@PathVariable Long systemId) {
        return ResponseEntity.ok(galaxyService.findGalaxyBySystemId(systemId));
    }

    @GetMapping(value = "/systems/galaxies")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find galaxy by system id in", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested galaxies",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findGalaxyBySystemId(@RequestParam List<Long> systemIds) {
        return ResponseEntity.ok(galaxyService.findGalaxyBySystemIdIn(systemIds));
    }

    @PostMapping("/galaxies")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
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
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateGalaxy(@PathVariable Long galaxyId,
                                          @Valid @RequestBody UpdateGalaxyDto galaxy) {
        return ResponseEntity.ok(galaxyService.updateGalaxy(galaxyId, galaxy));
    }

    @DeleteMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> deleteGalaxy(@PathVariable Long galaxyId) {
        return ResponseEntity.ok(galaxyService.deleteGalaxy(galaxyId));
    }
}
