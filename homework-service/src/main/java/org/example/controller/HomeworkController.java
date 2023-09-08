package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkDto;
import org.example.dto.homework.UpdateHomeworkDto;
import org.example.service.HomeworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homework-app")
public class HomeworkController {
    private final HomeworkService homeworkService;

    @GetMapping("theme/{themeId}/group/{groupId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework by theme id and group id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getHomeworkByThemeIdForGroup(@PathVariable("themeId") Integer themeId,
                                                          @PathVariable("groupId") Integer groupId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByThemeIdForGroup(themeId, groupId));
    }

    @PostMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
                                         @Valid @RequestBody CreateHomeworkDto homework) {
        return ResponseEntity.ok(homeworkService.addHomework(themeId, homework));
    }

    @PutMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
                                            @Valid @RequestBody UpdateHomeworkDto homework) {
        return ResponseEntity.ok(homeworkService.updateHomework(homeworkId, homework));
    }

    @DeleteMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
