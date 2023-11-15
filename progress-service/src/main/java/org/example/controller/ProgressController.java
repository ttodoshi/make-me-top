package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress-app")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;

    @GetMapping("/explorers/final-assessments")
    @PreAuthorize("isAuthenticated()") // TODO
    @Operation(summary = "Get explorer ids needed final assessment", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer ids",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerIdsNeededFinalAssessment(@RequestParam List<Integer> explorerIds) {
        return ResponseEntity.ok(
                progressService.getExplorerIdsNeededFinalAssessment(explorerIds)
        );
    }

    @GetMapping("/explorers/completed")
    @PreAuthorize("isAuthenticated()") // TODO
    @Operation(summary = "Get explorer ids with final assessment", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer ids",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerIdsWithFinalAssessment(@RequestParam List<Integer> explorerIds) {
        return ResponseEntity.ok(
                progressService.getExplorerIdsWithFinalAssessment(explorerIds));
    }

    @GetMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get courses progress by galaxy id", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy courses progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCoursesProgressForCurrentUser(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(
                progressService.getCoursesProgressForCurrentUser(galaxyId));
    }

    @GetMapping("/explorers/{explorerId}")
    @PreAuthorize("isAuthenticated()")
//    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER) && " +
//            "@roleService.hasAnyCourseRoleByExplorerId(#explorerId, T(org.example.config.security.role.CourseRoleType).EXPLORER)) || " +
//            "(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.hasAnyCourseRoleByExplorerId(#explorerId, T(org.example.config.security.role.CourseRoleType).KEEPER))")
    @Operation(summary = "Get explorer themes progress", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Themes progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerThemesProgress(@PathVariable Integer explorerId) {
        return ResponseEntity.ok(
                progressService.getExplorerThemesProgress(explorerId));
    }

    @GetMapping("/courses/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.config.security.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get explorer course progress", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerCourseProgress(@PathVariable Integer courseId) {
        return ResponseEntity.ok(
                progressService.getExplorerCourseProgress(courseId));
    }
}
