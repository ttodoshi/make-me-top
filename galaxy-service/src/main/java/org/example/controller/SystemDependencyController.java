package org.example.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.model.dependencyModel.CreateDependencyModel;
import org.example.model.dependencyModel.DeleteDependencyModel;
import org.example.service.DependencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dependency/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@JsonIgnoreProperties({"trace"})
public class SystemDependencyController {
    private final DependencyService dependencyService;

    @PostMapping
    @Secured("ROLE_BIG_BROTHER")
    @Operation(summary = "Add dependency", tags = "dependency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dependency created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addDependency(@RequestBody List<CreateDependencyModel> dependency) {
        return ResponseEntity.ok(dependencyService.addDependency(dependency));
    }

    @DeleteMapping
    @Secured("ROLE_BIG_BROTHER")
    @Operation(summary = "Delete dependency", tags = "dependency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dependency deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteDependency(@RequestBody DeleteDependencyModel model) {
        return ResponseEntity.ok(dependencyService.deleteDependency(model));
    }
}
