package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.model.PlanetModel;
import org.example.service.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet-app/planet/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlanetController {

    @Autowired
    PlanetService planetService;

    @GetMapping("{Id}")
    @Operation(summary = "get planet by system id", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public List<PlanetModel> getPlanetByIdSystem(@PathVariable("Id") Integer id) {
        return planetService.getListPlanetBySystemId(id);
    }

    @PostMapping("/create/{Id}")
    @Operation(summary = "create system", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void addPlanet(@PathVariable("Id") Integer galaxy_id, @RequestBody List<PlanetModel> list) {
        planetService.addPlanet(list, galaxy_id);
    }

    @DeleteMapping("{Id}")
    @Operation(summary = "delete system", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void deletePlanetById(@PathVariable("Id") Integer id) {
        planetService.deletePlanetById(id);
    }

    @PutMapping("{PlanetId}/galaxyId/{GalaxyId}")
    @Operation(summary = "delete system", tags = "planet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void updatePlanetById(@PathVariable("PlanetId") Integer planetId, @PathVariable("GalaxyId") Integer galaxyId, @RequestBody PlanetModel model) {
        planetService.updateSystem(planetId, galaxyId, model);
    }

}
