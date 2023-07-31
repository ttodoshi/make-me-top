package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.CourseProgressService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/explorer-cabinet/")
@RequiredArgsConstructor
public class CourseProgressController {
    private final CourseProgressService courseProgressService;

    @GetMapping("galaxy/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
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
        courseProgressService.setToken(token);
        return ResponseEntity.ok(
                courseProgressService.getCoursesProgressForCurrentUser(galaxyId));
    }

    @GetMapping("course/{courseId}")
    @PreAuthorize("@roleService.hasAnyCourseRole(#courseId, " +
            "T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get course planets progress for current user", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Systems progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemPlanetsProgress(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(
                courseProgressService.getThemesProgressByCourseId(courseId));
    }
}