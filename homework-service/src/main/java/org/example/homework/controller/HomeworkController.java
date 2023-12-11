package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.homework.CreateHomeworkDto;
import org.example.homework.dto.homework.UpdateHomeworkDto;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.service.HomeworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/homework-app")
public class HomeworkController {
    private final HomeworkService homeworkService;
    private final RoleService roleService;

    @GetMapping("/homeworks/{homeworkId}")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).EXPLORER)) ||" +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework by homework id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homeworks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworkByHomeworkId(@PathVariable Long homeworkId) {
        return ResponseEntity.ok(homeworkService.findHomeworkByHomeworkId(homeworkId));
    }

    @GetMapping("/themes/{themeId}/groups/{groupId}/homeworks")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.homework.enums.CourseRoleType).EXPLORER)) ||" +
            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> findHomeworkByCourseThemeIdAndGroupId(@PathVariable Long themeId,
                                                                   @PathVariable Long groupId) {
        return ResponseEntity.ok(homeworkService.findHomeworksByCourseThemeIdAndGroupId(themeId, groupId));
    }

    @GetMapping("/themes/{themeId}/groups/{groupId}/homeworks/completed")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.homework.enums.CourseRoleType).EXPLORER)) ||" +
            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get completed homework by theme id and group id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findCompletedHomeworkByThemeIdAndGroupIdForExplorers(@PathVariable Long themeId,
                                                                                  @PathVariable Long groupId,
                                                                                  @RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                homeworkService.findCompletedHomeworksByThemeIdAndGroupIdForExplorers(themeId, groupId, explorerIds)
        );
    }

    @GetMapping("/homeworks")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByHomeworkIds(#homeworkIds, T(org.example.homework.enums.CourseRoleType).EXPLORER))||" +
            "@roleService.hasAnyCourseRoleByHomeworkIds(#homeworkIds, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homeworks by homework id in", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homeworks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworksByHomeworkIdIn(@RequestParam List<Long> homeworkIds) {
        return ResponseEntity.ok(homeworkService.findHomeworksByHomeworkIdIn(homeworkIds));
    }

    @GetMapping("/themes/{themeId}/homeworks")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.homework.enums.CourseRoleType).EXPLORER)) ||" +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homeworks by theme id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homeworks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworksByThemeId(@PathVariable Long themeId) {
        return ResponseEntity.ok(
                roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER) ?
                        homeworkService.findHomeworksByThemeIdForExplorer(themeId) :
                        homeworkService.findHomeworksByThemeIdForKeeper(themeId)
        );
    }

    @PostMapping("/themes/{themeId}/homeworks")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> addHomework(@PathVariable Long themeId,
                                         @Valid @RequestBody CreateHomeworkDto homework) {
        return ResponseEntity.ok(homeworkService.addHomework(themeId, homework));
    }

    @PutMapping("/homeworks/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> updateHomework(@PathVariable Long homeworkId,
                                            @Valid @RequestBody UpdateHomeworkDto homework) {
        return ResponseEntity.ok(homeworkService.updateHomework(homeworkId, homework));
    }

    @DeleteMapping("/homeworks/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> deleteHomework(@PathVariable Long homeworkId) {
        return ResponseEntity.ok(homeworkService.deleteHomework(homeworkId));
    }
}
