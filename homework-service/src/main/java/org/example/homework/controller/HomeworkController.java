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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
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
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> findHomeworkByHomeworkId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @AuthenticationPrincipal Long authenticatedPersonId,
                                                      @PathVariable Long homeworkId) {
        return ResponseEntity.ok(
                homeworkService.findHomeworkByHomeworkId(authorizationHeader, authenticatedPersonId, homeworkId)
        );
    }

    @GetMapping("/themes/{themeId}/groups/{groupId}/homeworks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> findHomeworkByCourseThemeIdAndGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                   @CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                                   @PathVariable Long themeId,
                                                                   @PathVariable Long groupId) {
        return ResponseEntity.ok(
                homeworkService.findHomeworksByCourseThemeIdAndGroupId(authorizationHeader, authentication, themeId, groupId)
        );
    }

    @GetMapping("/themes/groups/{groupId}/homeworks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get homeworks by theme id in and group id", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested homework",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworkByCourseThemeIdInAndGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                     @AuthenticationPrincipal Long authenticatedPersonId,
                                                                     @PathVariable Long groupId,
                                                                     @RequestParam List<Long> themeIds) {
        return ResponseEntity.ok(
                homeworkService.findHomeworkByCourseThemeIdInAndGroupId(authorizationHeader, authenticatedPersonId, themeIds, groupId)
        );
    }

    @GetMapping("/themes/groups/{groupId}/homeworks/completed")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> findCompletedHomeworkByThemeIdAndGroupIdForExplorers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                                  @AuthenticationPrincipal Long authenticatedPersonId,
                                                                                  @PathVariable Long groupId,
                                                                                  @RequestParam List<Long> themeIds,
                                                                                  @RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                homeworkService.findCompletedHomeworksByCourseThemeIdInAndGroupIdForExplorers(
                        authorizationHeader, authenticatedPersonId, themeIds, groupId, explorerIds
                )
        );
    }

    @GetMapping("/homeworks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> findHomeworksByHomeworkIdIn(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @AuthenticationPrincipal Long authenticatedPersonId,
                                                         @RequestParam List<Long> homeworkIds) {
        return ResponseEntity.ok(
                homeworkService.findHomeworksByHomeworkIdIn(
                        authorizationHeader, authenticatedPersonId, homeworkIds
                )
        );
    }

    @GetMapping("/themes/{themeId}/homeworks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> findHomeworksByThemeId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                    @PathVariable Long themeId) {
        return ResponseEntity.ok(
                roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.EXPLORER) ?
                        homeworkService.findHomeworksByThemeIdForExplorer(authorizationHeader, (Long) authentication.getPrincipal(), themeId) :
                        homeworkService.findHomeworksByThemeIdForKeeper(authorizationHeader, (Long) authentication.getPrincipal(), themeId)
        );
    }

    @PostMapping("/themes/{themeId}/homeworks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> addHomework(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @AuthenticationPrincipal Long authenticatedPersonId,
                                         @PathVariable Long themeId,
                                         @Valid @RequestBody CreateHomeworkDto homework) {
        return ResponseEntity.ok(
                homeworkService.addHomework(authorizationHeader, authenticatedPersonId, themeId, homework)
        );
    }

    @PutMapping("/homeworks/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> updateHomework(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                            @AuthenticationPrincipal Long authenticatedPersonId,
                                            @PathVariable Long homeworkId,
                                            @Valid @RequestBody UpdateHomeworkDto homework) {
        return ResponseEntity.ok(
                homeworkService.updateHomework(authorizationHeader, authenticatedPersonId, homeworkId, homework)
        );
    }

    @DeleteMapping("/homeworks/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> deleteHomework(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                            @AuthenticationPrincipal Long authenticatedPersonId,
                                            @PathVariable Long homeworkId) {
        return ResponseEntity.ok(
                homeworkService.deleteHomework(authorizationHeader, authenticatedPersonId, homeworkId)
        );
    }
}
