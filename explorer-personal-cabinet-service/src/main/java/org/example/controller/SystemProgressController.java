package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.service.SystemProgressService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/explorer/")
@RequiredArgsConstructor
public class SystemProgressController {
    private final SystemProgressService systemProgressService;

    // TODO

    @GetMapping("galaxy/{galaxyId}")
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get systems progress for current user", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Systems progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemsProgress(@PathVariable("galaxyId") Integer galaxyId,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        return ResponseEntity.ok(
                systemProgressService.getSystemsProgressForCurrentUser(galaxyId, token));
    }

    @PatchMapping("/course/{courseId}")
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Update course progress for current user", tags = "course progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateCourseProgress(@PathVariable("courseId") Integer courseId,
                                                  @RequestBody ProgressUpdateRequest updateRequest) {
        return ResponseEntity.ok(
                systemProgressService.updateCourseProgress(courseId, updateRequest));
    }
}
