package org.example.progress.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.progress.service.CourseThemesProgressService;
import org.example.progress.service.ProgressService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress-app")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final CourseThemesProgressService courseThemesProgressService;

    @GetMapping("/explorers/final-assessments")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.progress.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> getExplorerIdsNeededFinalAssessment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                 @AuthenticationPrincipal Long authenticatedPersonId,
                                                                 @RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                progressService.getExplorerIdsNeededFinalAssessment(authorizationHeader, authenticatedPersonId, explorerIds)
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
                progressService.getExplorerIdsWithFinalAssessment(explorerIds)
        );
    }

    @GetMapping("/explorers/themes/marks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get themes marks for explorers", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Themes marks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorersThemesMarks(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                courseThemesProgressService.getExplorersThemesMarks(explorerIds)
        );
    }

    @GetMapping("/galaxies/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.progress.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> getCoursesProgressForCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                              @AuthenticationPrincipal Long authenticatedPersonId,
                                                              @PathVariable Long galaxyId) {
        return ResponseEntity.ok(
                progressService.getCoursesProgressForCurrentUser(authorizationHeader, authenticatedPersonId, galaxyId));
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
    public ResponseEntity<?> getExplorerThemesProgress(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @PathVariable Long explorerId) {
        return ResponseEntity.ok(
                progressService.getExplorerThemesProgress(authorizationHeader, explorerId));
    }

    @GetMapping("/courses/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.progress.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> getExplorerCourseProgress(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @AuthenticationPrincipal Long authenticatedPersonId,
                                                       @PathVariable Long courseId) {
        return ResponseEntity.ok(
                progressService.getExplorerCourseProgress(authorizationHeader, authenticatedPersonId, courseId)
        );
    }
}
