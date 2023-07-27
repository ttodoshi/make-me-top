package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.homework.HomeworkCreateRequest;
import org.example.dto.homework.HomeworkUpdateRequest;
import org.example.service.HomeworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    @GetMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)") // TODO
    @Operation(summary = "Get homework for theme", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Theme homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getHomeworkByThemeId(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByThemeId(themeId));
    }

    @PostMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Add homework to theme", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Add theme homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> addHomework(@PathVariable("themeId") Integer themeId,
                                         @Valid @RequestBody HomeworkCreateRequest homework) {
        return ResponseEntity.ok(homeworkService.addHomework(themeId, homework));
    }

    @PutMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update homework to theme", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Update theme homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateHomework(@PathVariable("homeworkId") Integer homeworkId,
                                            @Valid @RequestBody HomeworkUpdateRequest homework) {
        return ResponseEntity.ok(homeworkService.updateHomework(homeworkId, homework));
    }

    @DeleteMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Delete homework to theme", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete theme homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteHomework(@PathVariable("homeworkId") Integer homeworkId) {
        return ResponseEntity.ok(homeworkService.deleteHomework(homeworkId));
    }
}
