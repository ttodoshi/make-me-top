package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.theme.CourseThemeCreateRequest;
import org.example.dto.theme.CourseThemeUpdateRequest;
import org.example.service.CourseThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course-app/")
public class CourseThemeController {
    private final CourseThemeService courseThemeService;

    @GetMapping("theme/{themeId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.CourseRoleType).EXPLORER) ||" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Get theme by theme id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme by theme id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseThemeById(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(courseThemeService.getCourseTheme(themeId));
    }

    @GetMapping("course/{courseId}/theme")
    @PreAuthorize("@roleService.hasAnyCourseRole(#courseId, T(org.example.model.CourseRoleType).EXPLORER) ||" +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.model.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Get themes by course id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Themes by course id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseThemesByCourseId(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseThemeService.getCourseThemesByCourseId(courseId));
    }

    @PostMapping("course/{courseId}/theme")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> createTheme(@Valid @RequestBody CourseThemeCreateRequest theme,
                                         @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseThemeService.createCourseTheme(theme, courseId));
    }


    @PutMapping("theme/{themeId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateCourseTheme(@Valid @RequestBody CourseThemeUpdateRequest theme,
                                               @PathVariable Integer themeId) {
        return ResponseEntity.ok(courseThemeService.updateCourseTheme(theme, themeId));
    }
}
