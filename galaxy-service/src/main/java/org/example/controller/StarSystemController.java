package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.model.modelDAO.StarSystem;
import org.example.model.systemModel.SystemCreateModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.service.SystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/galaxy-app/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StarSystemController {
    private final SystemService systemService;

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
                                           @RequestParam(name = "withDependency", required = false) Boolean withDependency) {
        if (withDependency != null && withDependency)
            return ResponseEntity.ok(systemService.getStarSystemById(id));
        else
            return ResponseEntity.ok(systemService.getStarSystemByIdWithoutDependency(id));
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
    public void createSystem(@RequestBody SystemCreateModel model, @PathVariable("galaxyId") Integer id) {
        systemService.createSystem(model, id);
    }


    @PutMapping("galaxy/{galaxyId}/system")
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
    public void updateSystem(@RequestBody SystemCreateModel model, @PathVariable("galaxyId") Integer id) {
        systemService.updateSystem(model, id);
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
    public void deleteSystem(@PathVariable("systemId") Integer id) {
        systemService.deleteSystem(id);
    }
}
