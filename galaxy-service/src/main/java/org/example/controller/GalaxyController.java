package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.model.galaxyModel.CreateGalaxyModel;
import org.example.model.galaxyModel.GalaxyModel;
import org.example.model.galaxyModel.GalaxyWithOrbitModel;
import org.example.model.modelDAO.Galaxy;
import org.example.service.GalaxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/galaxy-app/galaxy/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GalaxyController {

    @Autowired
    GalaxyService galaxyService;

    @GetMapping("{ID}")
    @Operation(summary = "Get galaxy by Id", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galactic discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public GalaxyWithOrbitModel getGalaxyById(@PathVariable("ID") Integer id) {
        return galaxyService.getGalaxyById(id);
    }

    @PostMapping("create")
    @Operation(summary = "Create Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galactic discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void createGalaxy(@RequestBody CreateGalaxyModel model) {
        galaxyService.createGalaxy(model);
    }


    @PutMapping("{ID}")
    @Operation(summary = "Update Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galactic discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void updateGalaxy(@RequestBody GalaxyModel model, @PathVariable("ID") Integer id) {
        galaxyService.updateGalaxy(id, model);
    }

    @DeleteMapping("{ID}")
    @Operation(summary = "Delete Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galactic discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void deleteGalaxy(@PathVariable("ID") Integer id) {
        galaxyService.deleteGalaxy(id);
    }

    @GetMapping("")
    @Operation(summary = "Get all Galaxy", tags = "galaxy")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galactic discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public List<Galaxy> getAllGalaxy() {
        return galaxyService.getAllGalaxy();
    }

}
