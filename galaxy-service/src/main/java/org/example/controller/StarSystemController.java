package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.service.StarSystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/galaxy-app/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StarSystemController {
    private final StarSystemService starSystemService;

    @GetMapping("system/{systemId}")
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
    @Operation(summary = "Get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System by id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemById(@PathVariable("systemId") Integer id,
                                           @RequestParam(name = "withDependencies", required = false) Boolean withDependencies) {
        if (withDependencies != null && withDependencies)
            return ResponseEntity.ok(starSystemService.getStarSystemByIdWithDependencies(id));
        else
            return ResponseEntity.ok(starSystemService.getStarSystemById(id));
    }

    @PostMapping("galaxy/{galaxyId}/system")
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> createSystem(@RequestBody StarSystemDTO starSystem, @PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(starSystemService.createSystem(starSystem, galaxyId));
    }


    @PutMapping("galaxy/{galaxyId}/system/{systemId}")
    @Secured("ROLE_BIG_BROTHER")
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
    @Secured("ROLE_BIG_BROTHER")
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
    public ResponseEntity<?> deleteSystem(@PathVariable("systemId") Integer id) {
        return ResponseEntity.ok(starSystemService.deleteSystem(id));
    }
}
