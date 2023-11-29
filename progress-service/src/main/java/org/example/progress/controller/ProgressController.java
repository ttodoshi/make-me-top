package org.example.progress.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.progress.service.ProgressService;
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
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.progress.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.isExplorersKeeper(#explorerIds)")
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
    public ResponseEntity<?> getExplorerIdsNeededFinalAssessment(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                progressService.getExplorerIdsNeededFinalAssessment(explorerIds)
        );
    }

    @GetMapping("/explorers/completed")
    @PreAuthorize("isAuthenticated()")
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
    public ResponseEntity<?> getExplorerIdsWithFinalAssessment(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                progressService.getExplorerIdsWithFinalAssessment(explorerIds));
    }

    @GetMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.progress.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> getCoursesProgressForCurrentUser(@PathVariable("galaxyId") Long galaxyId) {
        return ResponseEntity.ok(
                progressService.getCoursesProgressForCurrentUser(galaxyId));
    }

    @GetMapping("/explorers/{explorerId}")
    @PreAuthorize("isAuthenticated()")
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
    public ResponseEntity<?> getExplorerThemesProgress(@PathVariable Long explorerId) {
        return ResponseEntity.ok(
                progressService.getExplorerThemesProgress(explorerId));
    }

    @GetMapping("/courses/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.progress.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.progress.enums.CourseRoleType).EXPLORER)")
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
    public ResponseEntity<?> getExplorerCourseProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                progressService.getExplorerCourseProgress(courseId));
    }
}
