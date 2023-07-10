package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.CreateGalaxyRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.service.GalaxyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/galaxy-app/galaxy/")
@RequiredArgsConstructor
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
    public ResponseEntity<?> getGalaxyById(@PathVariable("galaxyId") Integer id) {
        return ResponseEntity.ok(galaxyService.getGalaxyById(id));
    }

    @PostMapping
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> createGalaxy(@RequestBody CreateGalaxyRequest model,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        galaxyService.setToken(token);
        return ResponseEntity.ok(galaxyService.createGalaxy(model));
    }


    @PutMapping("{galaxyId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateGalaxy(@RequestBody GalaxyDTO galaxy,
                                          @PathVariable("galaxyId") Integer id) {
        return ResponseEntity.ok(galaxyService.updateGalaxy(id, galaxy));
    }

    @DeleteMapping("{galaxyId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
