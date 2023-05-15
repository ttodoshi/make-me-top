package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.model.modelDAO.Orbit;
import org.example.model.orbitModel.OrbitCreateModel;
import org.example.model.orbitModel.OrbitModel;
import org.example.model.orbitModel.OrbitWithSystemModel;
import org.example.service.OrbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/galaxy-app/orbit/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrbitController {
    @Autowired
    OrbitService orbitService;

    @PostMapping("create")
    @Operation(summary = "Create Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void createOrbit(@RequestBody OrbitCreateModel model) {
        orbitService.createOrbit(model);
    }


    @GetMapping("systemList/{ID}")
    @Operation(summary = "Get Orbit withSystemList", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public OrbitWithSystemModel getOrbitWithSystemListById(@PathVariable("ID") Integer id) throws OrbitNotFoundException {
        return orbitService.getOrbitWithSystemList(id);
    }


    @GetMapping("{ID}")
    @Operation(summary = "Get Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public OrbitModel getOrbitById(@PathVariable("ID") Integer id) throws OrbitNotFoundException {
        return orbitService.getOrbitById(id);
    }
    @PutMapping("{ID}")
    @Operation(summary = "Update Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void updateOrbit(@PathVariable("ID") Integer id, @RequestBody Orbit orbit) throws OrbitNotFoundException {
        orbitService.updateOrbit(id, orbit);
    }

    @DeleteMapping("{ID}")
    @Operation(summary = "Delete Orbit", tags = "orbit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orbit discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void deleteOrbit(@PathVariable("ID")Integer id) throws OrbitNotFoundException {
        orbitService.deleteOrbit(id);
    }

}
