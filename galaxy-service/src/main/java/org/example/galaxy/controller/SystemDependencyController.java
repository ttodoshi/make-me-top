package org.example.galaxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.dto.dependency.DependencyDto;
import org.example.galaxy.service.SystemDependencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/galaxy-app/dependencies")
@RequiredArgsConstructor
@Validated
public class SystemDependencyController {
    private final SystemDependencyService systemDependencyService;

    @PostMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Create dependency", tags = "dependency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Dependency created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addDependency(@RequestBody List<@Valid CreateDependencyDto> dependency) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        systemDependencyService.addDependency(dependency)
                );
    }

    @DeleteMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.galaxy.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> deleteDependency(@Valid @RequestBody DependencyDto model) {
        return ResponseEntity.ok(systemDependencyService.deleteDependency(model));
    }
}
