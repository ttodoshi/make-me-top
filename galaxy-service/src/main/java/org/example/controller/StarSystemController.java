package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemRequest;
import org.example.service.StarSystemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/galaxy-app/")
@RequiredArgsConstructor
public class StarSystemController {
    private final StarSystemService starSystemService;

    @GetMapping("system/{systemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get system by systemId", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System by system id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemById(@PathVariable("systemId") Integer systemId,
                                           @RequestParam(name = "withDependencies", required = false) Boolean withDependencies) {
        if (withDependencies != null && withDependencies)
            return ResponseEntity.ok(starSystemService.getStarSystemByIdWithDependencies(systemId));
        else
            return ResponseEntity.ok(starSystemService.getStarSystemById(systemId));
    }

    @GetMapping("galaxy/{galaxyId}/system")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get systems by galaxy id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Systems by galaxy id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemsByGalaxyId(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(starSystemService.getStarSystemsByGalaxyId(galaxyId));
    }

    @PostMapping("galaxy/{galaxyId}/system")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create system", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createSystem(@RequestBody StarSystemRequest starSystem,
                                          @PathVariable("galaxyId") Integer galaxyId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        starSystemService.setToken(token);
        return ResponseEntity.ok(starSystemService.createSystem(starSystem, galaxyId));
    }


    @PutMapping("galaxy/{galaxyId}/system/{systemId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateSystem(@RequestBody StarSystemDTO starSystem,
                                          @PathVariable("galaxyId") Integer galaxyId,
                                          @PathVariable("systemId") Integer systemId) {
        return ResponseEntity.ok(starSystemService.updateSystem(starSystem, galaxyId, systemId));
    }

    @DeleteMapping("system/{systemId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> deleteSystem(@PathVariable("systemId") Integer systemId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        starSystemService.setToken(token);
        return ResponseEntity.ok(starSystemService.deleteSystem(systemId));
    }
}
