package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.theme.UpdateCourseThemeDto;
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
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleServiceImpl.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleServiceImpl.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Get theme by theme id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested theme",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseThemeById(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(courseThemeService.getCourseTheme(themeId));
    }

    @GetMapping("course/{courseId}/theme")
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleServiceImpl.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleServiceImpl.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Get themes by course id", tags = "theme")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested themes by course id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseThemesByCourseId(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseThemeService.getCourseThemesByCourseId(courseId));
    }

    @PutMapping("theme/{themeId}")
    @PreAuthorize("@roleServiceImpl.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateCourseTheme(@Valid @RequestBody UpdateCourseThemeDto theme,
                                               @PathVariable Integer themeId) {
        return ResponseEntity.ok(courseThemeService.updateCourseTheme(themeId, theme));
    }
}
