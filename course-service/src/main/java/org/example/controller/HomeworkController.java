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

    @GetMapping("group/{groupId}/homework")
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRoleByGroupId(#groupId, T(org.example.model.role.CourseRoleType).EXPLORER) ||" +
            "@roleServiceImpl.hasAnyCourseRoleByGroupId(#groupId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework by group id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested group homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getHomeworkByGroupId(@PathVariable("groupId") Integer groupId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByGroupId(groupId));
    }

    @PostMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
    @PreAuthorize("@roleServiceImpl.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
