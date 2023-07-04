package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.CourseThemeCreateRequest;
import org.example.dto.CourseThemeUpdateRequest;
import org.example.service.CourseThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course-app/")
public class CourseThemeController {
    private final CourseThemeService courseThemeService;

    @GetMapping("theme/{themeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get theme by theme id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme by themeId",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseThemeById(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(courseThemeService.getCourseTheme(themeId));
    }

    @PostMapping("course/{courseId}/theme")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create theme", tags = "theme", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createTheme(@RequestBody CourseThemeCreateRequest theme,
                                         @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseThemeService.createCourseTheme(theme, courseId));
    }


    @PutMapping("theme/{themeId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update theme by id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateCourseTheme(@RequestBody CourseThemeUpdateRequest theme,
                                               @PathVariable Integer themeId) {
        return ResponseEntity.ok(courseThemeService.updateCourseTheme(theme, themeId));
    }

    @DeleteMapping("theme/{themeId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete theme by id", tags = "theme", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteCourseTheme(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(courseThemeService.deleteCourseTheme(themeId));
    }
}
