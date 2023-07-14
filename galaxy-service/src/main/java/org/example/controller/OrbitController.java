package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.CreateOrbitWithStarSystems;
import org.example.dto.orbit.OrbitDTO;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.service.OrbitService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/galaxy-app/")
@RequiredArgsConstructor
public class OrbitController {
    private final OrbitService orbitService;

    @GetMapping("orbit/{orbitId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit by id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getOrbitById(@PathVariable("orbitId") Integer id,
                                          @RequestParam(name = "withSystemsList", required = false) Boolean withSystemsList) throws OrbitNotFoundException {
        if (withSystemsList != null && withSystemsList)
            return ResponseEntity.ok(orbitService.getOrbitWithSystemList(id));
        else
            return ResponseEntity.ok(orbitService.getOrbitById(id));
    }

    @PostMapping("galaxy/{galaxyId}/orbit")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createOrbit(@Valid @RequestBody CreateOrbitWithStarSystems orbit,
                                         @PathVariable Integer galaxyId,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        orbitService.setToken(token);
        return ResponseEntity.ok(orbitService.createOrbit(galaxyId, orbit));
    }

    @PutMapping("orbit/{orbitId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateOrbit(@PathVariable("orbitId") Integer id,
                                         @Valid @RequestBody OrbitDTO orbit) {
        return ResponseEntity.ok(orbitService.updateOrbit(id, orbit));
    }

    @DeleteMapping("orbit/{orbitId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteOrbit(@PathVariable("orbitId") Integer orbitId) throws OrbitNotFoundException {
        return ResponseEntity.ok(orbitService.deleteOrbit(orbitId));
    }
}
