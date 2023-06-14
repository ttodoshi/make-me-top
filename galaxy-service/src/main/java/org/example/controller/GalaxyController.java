package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.exception.galaxyEX.GalaxyNotFoundException;
import org.example.model.galaxyModel.CreateGalaxyModel;
import org.example.model.galaxyModel.GalaxyModel;
import org.example.model.modelDAO.Galaxy;
import org.example.service.GalaxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/galaxy-app/galaxy/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GalaxyController {
    private final GalaxyService galaxyService;

    @GetMapping
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
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
    public List<Galaxy> getAllGalaxies() {
        return galaxyService.getAllGalaxies();
    }

    @GetMapping("{galaxyId}")
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
        try {
            return ResponseEntity.ok(galaxyService.getGalaxyById(id));
        } catch (Exception e) {
            throw new GalaxyNotFoundException();
        }
    }

    @PostMapping
    @Secured("ROLE_BIG_BROTHER")
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
    public void createGalaxy(@RequestBody CreateGalaxyModel model) {
        galaxyService.createGalaxy(model);
    }


    @PutMapping("{galaxyId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public void updateGalaxy(@RequestBody GalaxyModel model, @PathVariable("galaxyId") Integer id) {
        galaxyService.updateGalaxy(id, model);
    }

    @DeleteMapping("{galaxyId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public void deleteGalaxy(@PathVariable("galaxyId") Integer id) {
        galaxyService.deleteGalaxy(id);
    }


}
