package org.example.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.DependencyRequest;
import org.example.dto.dependency.CreateDependencyRequest;
import org.example.service.DependencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/galaxy-app/dependency/")
@RequiredArgsConstructor
@JsonIgnoreProperties({"trace"})
@Validated
public class SystemDependencyController {
    private final DependencyService dependencyService;

    @PostMapping
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> addDependency(@RequestBody List<@Valid CreateDependencyRequest> dependency) {
        return ResponseEntity.ok(dependencyService.addDependency(dependency));
    }

    @DeleteMapping
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> deleteDependency(@Valid @RequestBody DependencyRequest model) {
        return ResponseEntity.ok(dependencyService.deleteDependency(model));
    }
}
