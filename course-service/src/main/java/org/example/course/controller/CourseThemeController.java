package org.example.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.course.service.CourseThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-app")
public class CourseThemeController {
    private final CourseThemeService courseThemeService;

    @GetMapping("/themes/{themeId}")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.course.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.course.enums.CourseRoleType).EXPLORER)) ||" +
            "(@roleService.hasAnyAuthenticationRole(T(org.example.course.enums.AuthenticationRoleType).KEEPER) &&" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.course.enums.CourseRoleType).KEEPER)) ||" +
            "@roleService.hasAnyAuthenticationRole(T(org.example.course.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> findCourseThemeById(@PathVariable Long themeId) {
        return ResponseEntity.ok(courseThemeService.findCourseThemeById(themeId));
    }

    @PutMapping("/themes/{themeId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.course.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateCourseTheme(@PathVariable Long themeId,
                                               @Valid @RequestBody UpdateCourseThemeDto theme) {
        return ResponseEntity.ok(courseThemeService.updateCourseTheme(themeId, theme));
    }
}
