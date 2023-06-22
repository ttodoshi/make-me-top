package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.PlanetDTO;
import org.example.service.PlanetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet-app/")
@CrossOrigin(allowedHeaders = "*")
@RequiredArgsConstructor
public class PlanetController {
    private final PlanetService planetService;

    @GetMapping("system/{systemId}/planet")
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
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
    public ResponseEntity<?> getPlanetsBySystemId(
            @PathVariable("systemId") Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.getPlanetsListBySystemId(id));
    }

    @PostMapping("galaxy/{galaxyId}/planet")
    @Secured("ROLE_BIG_BROTHER")
    @Operation(summary = "Create planet", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Create planet",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addPlanet(@PathVariable("galaxyId") Integer galaxyId,
                                       @RequestBody List<PlanetDTO> planetList,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.addPlanet(planetList, galaxyId));
    }

    @DeleteMapping("planet/{planetId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> deletePlanetById(@PathVariable("planetId") Integer id) {
        return ResponseEntity.ok(planetService.deletePlanetById(id));
    }

    @PutMapping("galaxy/{galaxyId}/planet/{planetId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> updatePlanetById(
            @PathVariable("planetId") Integer planetId,
            @PathVariable("galaxyId") Integer galaxyId,
            @RequestBody PlanetDTO planet,
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        planetService.setToken(token);
        return ResponseEntity.ok(planetService.updatePlanet(planetId, galaxyId, planet));
    }
}
