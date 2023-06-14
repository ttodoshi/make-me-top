package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.exception.SystemNotFoundException;
import org.example.model.PlanetModel;
import org.example.service.PlanetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/planet-app/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
    public ResponseEntity<?> getPlanetsBySystemId(@PathVariable("systemId") Integer id,
                                                  HttpServletRequest request) {
        try {
            planetService.setAuthHeader(request.getHeader("Authorization"));
            return ResponseEntity.ok(planetService.getPlanetsListBySystemId(id));
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
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
    public void addPlanet(@PathVariable("galaxyId") Integer galaxyId,
                          @RequestBody List<PlanetModel> list,
                          HttpServletRequest request) {
        planetService.setAuthHeader(request.getHeader("Authorization"));
        planetService.addPlanet(list, galaxyId);
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
    public void deletePlanetById(@PathVariable("planetId") Integer id) {
        planetService.deletePlanetById(id);
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
    public void updatePlanetById(@PathVariable("planetId") Integer planetId,
                                 @PathVariable("galaxyId") Integer galaxyId,
                                 @RequestBody PlanetModel model,
                                 HttpServletRequest request) {
        planetService.setAuthHeader(request.getHeader("Authorization"));
        planetService.updateSystem(planetId, galaxyId, model);
    }
}
