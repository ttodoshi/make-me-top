package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.service.MarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/keeper-cabinet")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;

    @PostMapping("mark")
    @PreAuthorize("@roleService.hasAnyCourseRoleByExplorerId(#courseMark.explorerId," +
            "T(org.example.model.role.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> setCourseMark(@Valid @RequestBody MarkDTO courseMark) {
        return ResponseEntity.ok(markService.setCourseMark(courseMark));
    }

    @PostMapping("theme/{themeId}/mark")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId," +
            "T(org.example.model.role.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> setThemeMark(@PathVariable("themeId") Integer themeId,
                                          @Valid @RequestBody MarkDTO completeThemeRequest) {
        return ResponseEntity.ok(markService.setThemeMark(themeId, completeThemeRequest));
    }
}
