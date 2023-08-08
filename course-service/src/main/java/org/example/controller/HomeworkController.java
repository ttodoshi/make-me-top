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
@RequestMapping("/course-app/")
public class HomeworkController {
    private final HomeworkService homeworkService;

    @GetMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER) ||" +
            "@roleService.hasAnyGeneralRole(T(org.example.model.role.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Get homework by theme id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested theme homework",
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
    @Operation(summary = "Create homework for theme", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework created",
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
    @Operation(summary = "Update homework by id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework updated",
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
    @Operation(summary = "Delete homework by id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteHomework(@PathVariable("homeworkId") Integer homeworkId) {
        return ResponseEntity.ok(homeworkService.deleteHomework(homeworkId));
    }
}
