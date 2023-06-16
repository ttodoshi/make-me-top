package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.model.modelDAO.Orbit;
import org.example.model.orbitModel.OrbitCreateModel;
import org.example.service.OrbitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orbit/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrbitController {
    private final OrbitService orbitService;

    @GetMapping("{orbitId}")
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
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

    @PostMapping
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> createOrbit(@RequestBody OrbitCreateModel model) {
        return ResponseEntity.ok(orbitService.createOrbit(model));
    }

    @PutMapping("{orbitId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> updateOrbit(@PathVariable("orbitId") Integer id, @RequestBody Orbit orbit) throws OrbitNotFoundException {
        return ResponseEntity.ok(orbitService.updateOrbit(id, orbit));
    }

    @DeleteMapping("{orbitId}")
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> deleteOrbit(@PathVariable("orbitId") Integer id) throws OrbitNotFoundException {
        return ResponseEntity.ok(orbitService.deleteOrbit(id));
    }
}
