package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.galaxy.GalaxyCreateRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.service.GalaxyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/galaxy-app/galaxy/")
@RequiredArgsConstructor
@Slf4j
public class GalaxyController {
    private final GalaxyService galaxyService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all Galaxies", tags = "galaxy")
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
        return ResponseEntity.ok(galaxyService.getAllGalaxies());
    }

    @GetMapping("{galaxyId}")
    @PreAuthorize("isAuthenticated()")
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
    @Operation(summary = "Get galaxy by Id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy by id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getGalaxyById(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(galaxyService.getGalaxyById(galaxyId));
    }

    @PostMapping
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createGalaxy(@Valid @RequestBody GalaxyCreateRequest model,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        galaxyService.setToken(token);
        return ResponseEntity.ok(galaxyService.createGalaxy(model));
    }


    @PutMapping("{galaxyId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateGalaxy(@Valid @RequestBody GalaxyDTO galaxy,
                                          @PathVariable("galaxyId") Integer id) {
        return ResponseEntity.ok(galaxyService.updateGalaxy(id, galaxy));
    }

    @DeleteMapping("{galaxyId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete Galaxy", tags = "galaxy")
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
}
