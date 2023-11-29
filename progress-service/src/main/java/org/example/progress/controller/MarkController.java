package org.example.progress.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.service.MarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/progress-app")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;

    @GetMapping("/explorers/{explorerId}/mark")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get course mark by explorer id", tags = "mark")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested mark",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseMark(@PathVariable Long explorerId) {
        return ResponseEntity.ok(markService.getCourseMark(explorerId));
    }

    @PostMapping("/marks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.progress.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByExplorerId(#courseMark.explorerId, T(org.example.progress.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Set course mark from 1 to 5 to explorer", tags = "mark")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course completed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setCourseMark(@Valid @RequestBody MarkDto courseMark) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        markService.setCourseMark(courseMark)
                );
    }

    @PostMapping("/themes/{themeId}/marks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.progress.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.progress.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Set theme mark from 1 to 5 to explorer", tags = "mark")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course theme completed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setThemeMark(@PathVariable("themeId") Long themeId,
                                          @Valid @RequestBody MarkDto completeThemeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        markService.setThemeMark(themeId, completeThemeRequest)
                );
    }
}
